package ru.hse.surkov.hw07;

import java.util.Arrays;

import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple pool of workers with fixed number of workers inside.
 * */
public class ThreadPool {

    @NotNull private Thread[] workers;
    @NotNull private ConcurrentQueue<Task<?>> queue = new ConcurrentQueue<>();
    private volatile boolean shutdown = false;

    public ThreadPool(int numberOfThreads) {
        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException(
                    "Number of workers should be a positive number, but found " + numberOfThreads);
        }
        workers = new Thread[numberOfThreads];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Thread(this::workerForkFlow);
            workers[i].start();
        }
    }

    private void workerForkFlow() {
        try {
            while (!Thread.interrupted() && !shutdown) {
                Task<?> task = queue.pop();
                task.process();
            }
        } catch (InterruptedException ignored) {
            /*
            * According to the requirements of the problem,
            * in this case worker should be finished without
            * any exceptions.
            * */
        }
    }

    /**
     * Adds a new task into pool.
     *
     * @param supplier which corresponds to the task
     * */
    @NotNull public <T> Task<T> submit(@NotNull Supplier<T> supplier) {
        if (shutdown) {
            throw new IllegalCallerException(
                    "Shutdown has been already executed, hence pool is not able to calculate any task");
        }
        Task<T> task = new Task<>(supplier);
        queue.push(task);
        return task;
    }

    /**
     * Interrupts work of all workers.
     * */
    public void shutdown() {
        if (shutdown) {
            return;
        }
        shutdown = true;
        Arrays.stream(workers).forEach(Thread::interrupt);
    }

    private enum TaskState {
        NOT_READY_TO_START,
        READY_TO_START,
        IN_PROCESS,
        FINISHED_SUCCESSFULLY,
        FINISHED_WITH_EXCEPTION
    }

    private class Task<T> implements LightFuture<T> {

        @Nullable private Task<?> parentTask;
        @Nullable private Function<?, T> parentFunction;

        @Nullable private T data;
        @Nullable private LightExecutionException exception;

        @Nullable private Supplier<T> supplier;

        @NotNull private volatile TaskState state = TaskState.NOT_READY_TO_START;

        /*
        * Takes dependent tasks and generates supplier for it
        * by applying function, which is stored in the
        * dependent task to the current task's data.
        * */
        @SuppressWarnings("unchecked")
        @NotNull public <U> Supplier<U> generateDependentSupplier(@NotNull Task<U> dependentTask) {
            final T finalData = data;
            final Function<? super  T, U> function =
                    (Function<? super T, U>) dependentTask.parentFunction;
            if (function == null) {
                throw new IllegalStateException("Parent function should not be a null");
            }
            return () -> function.apply(finalData);
        }

        /*
        * This constructor used, when task to be constructed depends on
        * some other task and function, which should be applied to the
        * result of parent task calculation.
        * */
        public <U> Task(@NotNull Task<U> parentTask,
                        @NotNull Function<? super U, T> parentFunction) {
            this.parentTask = parentTask;
            this.parentFunction = parentFunction;
        }

        /*
        * Constructs independent task using given supplier.
        * */
        public Task(@NotNull Supplier<T> supplier) {
            this.supplier = supplier;
            state = TaskState.READY_TO_START;
        }

        @Override
        public boolean isReady() {
            return state.equals(TaskState.FINISHED_SUCCESSFULLY)
                    || state.equals(TaskState.FINISHED_WITH_EXCEPTION);
        }

        @Override
        @NotNull public T get() throws LightExecutionException {
            while (!isReady()) {
                try {
                    ThreadPool.this.wait();
                } catch (Exception e) {
                    throw new LightExecutionException(e);
                }
            }
            if (state.equals(TaskState.FINISHED_SUCCESSFULLY)) {
                if (data == null) {
                    throw new IllegalStateException("Data should not be a null");
                }
                return data;
            } else {
                if (exception == null) {
                    throw new IllegalStateException("Exception should not be a null");
                }
                throw exception;
            }
        }

        @Override
        @NotNull public <U> LightFuture<U> thenApply(@NotNull Function<? super T, U> function) {
            Task<U> task = new Task<>(this, function);
            queue.push(task);
            return task;
        }

        /*
        * If task is already done then broadcast notification
        * will be executed. If task depends on some other task then
        * methods will check whether parent task is ready to provide data
        * for generating supplier for current task. Finally, if current task
        * is ready to start then corresponded supplier will be executed.
        * */
        public void process() {
            if (isReady()) {
                notifyAll();
                return;
            }
            if (state.equals(TaskState.NOT_READY_TO_START)) {
                if (parentTask == null || !parentTask.isReady()) {
                    queue.push(this);
                    return;
                }
                supplier = parentTask.generateDependentSupplier(this);
                state = TaskState.READY_TO_START;
                queue.push(this);
            } else if (state.equals(TaskState.READY_TO_START)) {
                try {
                    if (supplier == null) {
                        throw new IllegalStateException("Supplier should not be a null");
                    }
                    state = TaskState.IN_PROCESS;
                    data = supplier.get();
                    state = TaskState.FINISHED_SUCCESSFULLY;
                } catch (Exception e) {
                    exception = new LightExecutionException(e);
                    state = TaskState.FINISHED_WITH_EXCEPTION;
                }
            }
        }
    }
}
