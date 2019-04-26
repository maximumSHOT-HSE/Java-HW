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
            while (!Thread.interrupted()) {
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

    public <T> Task<T> submit(@NotNull Supplier<T> supplier) {
        Task<T> task = new Task<>(supplier);
        queue.push(task);
        return task;
    }

    public void shutdown() {
        Arrays.stream(workers).forEach(Thread::interrupt);
    }

    public enum TaskState {
        NOT_READY_TO_START,
        READY_TO_START,
        IN_PROCESS,
        FINISHED_SUCCESSFULLY,
        FINISHED_WITH_EXCEPTION
    }

    private class Task<T> implements LightFuture<T> {

        @Nullable private Task<?> parentTask;
        @Nullable private Object parentFunction;

        @Nullable private T data;
        @Nullable private LightExecutionException exception;

        @Nullable private Supplier<T> supplier;

        @NotNull private volatile TaskState state = TaskState.NOT_READY_TO_START;

        @SuppressWarnings("unchecked")
        public <U> Supplier<U> generateDependentSupplier(@NotNull Task<U> dependentTask) {
            final T finalData = data;
            final Function<? super  T, U> function =
                    (Function<? super T, U>) dependentTask.parentFunction;
            if (function == null) {
                throw new IllegalStateException("Parent function should not be a null");
            }
            return () -> function.apply(finalData);
        }

        public <U> Task(@NotNull Task<U> parentTask,
                        @NotNull Function<? super U, T> parentFunction) {
            this.parentTask = parentTask;
            this.parentFunction = parentFunction;
        }

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
        public <U> LightFuture<U> thenApply(@NotNull Function<? super T, U> function) {
            Task<U> task = new Task<>(this, function);
            queue.push(task);
            return task;
        }

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
