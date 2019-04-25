package ru.hse.surkov.hw07;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Simple pool of workers with fixed number of workers inside.
 * */
public class ThreadPool {

    @NotNull private Thread[] workers;
    @NotNull public final Queue<Task<?>> queryQueue = new LinkedList<>();

    public ThreadPool(int threadsNumber) {
        if (threadsNumber <= 0) {
            throw new IllegalArgumentException(
                    "number of workers should be positive values");
        }
        workers = new Thread[threadsNumber];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Thread(this::workerWorkFlow);
            workers[i].start();
        }
    }

    private void workerWorkFlow() {
        while (true) {
            Task<?> task = null;
            synchronized (queryQueue) {
                while (queryQueue.isEmpty()) {
                    try {
                        queryQueue.wait();
                    } catch (InterruptedException ignored) {
                        /*
                        * Requirement of the problem, hence
                        * thread should be terminated successfully.
                        * */
                        return;
                    }
                }
                task = queryQueue.poll();
            }
            task.calculate();
        }
    }

    /**
     * Adds a new task into pool.
     * @param supplier which corresponds to the task
     * */
    @NotNull public <T> LightFuture<T> addTask(@NotNull Supplier<T> supplier) {
        Task<T> task = new Task<T>(supplier, TaskState.READY_TO_START);
        synchronized (queryQueue) {
            queryQueue.add(task);
            queryQueue.notifyAll();
        }
        return task;
    }

    /**
     * Interrupts work of all workers.
     * */
    public void shutdown() {
        Arrays.stream(workers).forEach(Thread::interrupt);
    }

    private enum TaskState {
        NOT_READY_TO_START,
        READY_TO_START,
        IN_PROGRESS,
        FINISHED_SUCCESSFULLY,
        FINISHED_WITH_EXCEPTION
    }

    private class Task<T> implements LightFuture<T> {

        @Nullable private Object parentFunction;
        @Nullable private Supplier<T> supplier;
        @NotNull private TaskState state;
        @NotNull private DataHolder<T> ownDataHolder = new DataHolder<>();
        @NotNull private final List<Task<?>> dependentTasks = new ArrayList<>();

        public Task(@Nullable Supplier<T> supplier, @NotNull TaskState state) {
            this.supplier = supplier;
            this.state = state;
        }

        @NotNull public synchronized TaskState getState() {
            return state;
        }

        public synchronized void setState(@NotNull TaskState state) {
            this.state = state;
        }

        /**
         * Receives data, which should be put into parent function and
         * using it constructs appropriate supplier.
         * */
        @SuppressWarnings("unchecked")
        public synchronized  <U> void receiveParentResult(U parentData) {
            final Function<U, T> function = (Function<U, T>) parentFunction;
            if (function == null) {
                throw new IllegalStateException("Parent function should be Non Null");
            }
            supplier = () -> function.apply(parentData);
        }

        public synchronized void setParentFunction(Object parentFunction) {
            this.parentFunction = parentFunction;
        }

        public synchronized void calculate() {
            state = TaskState.IN_PROGRESS;
            try {
                if (supplier == null) {
                    throw new IllegalStateException("supplier should be Non Null");
                }
                ownDataHolder.assignValue(supplier.get());
                state = TaskState.FINISHED_SUCCESSFULLY;
            } catch (Exception e) {
                ownDataHolder.assignException(new LightExecutionException(e));
                state = TaskState.FINISHED_WITH_EXCEPTION;
            }
            synchronized (dependentTasks) {
                var iterator = dependentTasks.iterator();
                while (iterator.hasNext()) {
                    var dependentTask = iterator.next();
                    dependentTask.setState(TaskState.READY_TO_START);
                    dependentTask.receiveParentResult(ownDataHolder.getData());
                    synchronized (queryQueue) {
                        queryQueue.add(dependentTask);
                        queryQueue.notifyAll();
                    }
                    iterator.remove();
                }
            }
        }

        @Override
        public synchronized boolean isReady() {
            return state.equals(TaskState.FINISHED_SUCCESSFULLY) ||
                    state.equals(TaskState.FINISHED_WITH_EXCEPTION);
        }

        @Override
        @Nullable public synchronized T get() throws LightExecutionException {
            if (!isReady()) {
                calculate();
            }
            if (state.equals(TaskState.FINISHED_SUCCESSFULLY)) {
                return ownDataHolder.getData();
            } else {
                throw ownDataHolder.getException();
            }
        }

        @Override
        public synchronized <U> LightFuture<U> thenApply(Function<? super T, U> function) {
            if (!isReady()) {
                System.out.println("LOL");
                Task<U> dependentTask = new Task<>(null, TaskState.NOT_READY_TO_START);
                dependentTask.setParentFunction(function);
                dependentTasks.add(dependentTask);
                if (!isReady()) {
                    System.out.println("?? " + dependentTasks.size());
                }
                return dependentTask;
            } else {
                System.out.println("KEK");
                final T data = ownDataHolder.getData();
                return addTask(() -> function.apply(data));
            }
        }
    }

    private class DataHolder<T> {

        @Nullable private T data;
        @Nullable LightExecutionException exception;

        public void assignValue(@Nullable T data) {
            this.data = data;
            exception = null;
        }

        public void assignException(@Nullable LightExecutionException exception) {
            data = null;
            this.exception = exception;
        }

        @Nullable public T getData() {
            return data;
        }

        @NotNull public LightExecutionException getException() {
            if (exception == null) {
                throw new IllegalStateException("Holded exception should be Non Null");
            }
            return exception;
        }
    }
}
