package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Processing unit responsible for executing tasks.
 */
public final class VirtualProcessorUnit
{
  /**
   * The executor responsible for selecting and invoking tasks.
   */
  @Nonnull
  private final Executor _executor;

  /**
   * Create the processor unit.
   *
   * @param executor the associated task executor.
   */
  public VirtualProcessorUnit( @Nonnull final Executor executor )
  {
    _executor = Objects.requireNonNull( executor );
    _executor.init( this::activate );
  }

  /**
   * Return the VirtualProcessorUnit that is currently executing.
   * This method MUST NOT be invoked if there is no {@link VirtualProcessorUnit} activated.
   *
   * @return the VirtualProcessorUnit that is currently executing.
   */
  @Nonnull
  public static VirtualProcessorUnit current()
  {
    return VirtualProcessorUnitCurrentHolder.current();
  }

  /**
   * Return the VirtualProcessorUnit that executes "macro" tasks.
   * This is the default VPU in the browser and indicates tasks that are scheduled via a
   * call to <code>setTimeout(callback,0)</code>.
   *
   * @return the macro VirtualProcessorUnit.
   */
  @Nonnull
  public static VirtualProcessorUnit macroTask()
  {
    return VirtualProcessorUnitsHolder.macroTask();
  }

  /**
   * Return the VirtualProcessorUnit that executes "micro" tasks.
   * The "micro" tasks are those that the browser executes after the current "macro" or "micro" task.
   * This VPU schedules an activation via a call to {@code new Promise().then( v -> callback() )}.
   *
   * @return the macro VirtualProcessorUnit.
   */
  @Nonnull
  public static VirtualProcessorUnit microTask()
  {
    return VirtualProcessorUnitsHolder.microTask();
  }

  /**
   * Return the VirtualProcessorUnit that executes "animationFrame" tasks.
   * The "animationFrame" tasks are invoked prior to the next frames render.
   * This VPU schedules an activation via a call to <code>requestAnimationFrame( callback )</code>.
   *
   * @return the macro VirtualProcessorUnit.
   */
  @Nonnull
  public static VirtualProcessorUnit animationFrame()
  {
    return VirtualProcessorUnitsHolder.animationFrame();
  }

  /**
   * Return the VirtualProcessorUnit that executes tasks after the next browser render.
   * The "afterFrame" tasks are invoked after the next frames render by responding to a message on a
   * MessageChannel that is sent in {@code requestAnimationFrame()}.
   *
   * @return the macro VirtualProcessorUnit.
   */
  @Nonnull
  public static VirtualProcessorUnit afterFrame()
  {
    return VirtualProcessorUnitsHolder.afterFrame();
  }

  /**
   * Queue the specified task for execution and enable the VirtualProcessorUnit for activation if necessary.
   * The task must not be already queued.
   *
   * @param task the task.
   */
  public void queue( @Nonnull final Task task )
  {
    _executor.queue( task );
  }

  @Nonnull
  public Task task( @Nonnull final Runnable work )
  {
    return new Task( work );
  }

  /**
   * Activate the unit.
   * This involves setting current unit, invoking the activation function and clearing the current unit.
   * It is an error to invoke this method if there is already a current unit.
   *
   * @param activationFn the activation function.
   * @see Context#activate(ActivationFn)
   */
  private synchronized void activate( @Nonnull final ActivationFn activationFn )
  {
    VirtualProcessorUnitCurrentHolder.activate( this );
    try
    {
      activationFn.invoke();
    }
    finally
    {
      VirtualProcessorUnitCurrentHolder.deactivate( this );
    }
  }

  /**
   * Interface via which the {@link VirtualProcessorUnit} executes tasks.
   * The executor is responsible for activating the underlying {@link VirtualProcessorUnit} when required.
   * Each time the {@link VirtualProcessorUnit} is activated it will use callback to talk to executor
   * and the executor is responsible for selecting and executing tasks until it decides to return control
   * to the {@link VirtualProcessorUnit}.
   */
  interface Executor
  {
    /**
     * Initialize the executor passing in the context associated with the underlying {@link VirtualProcessorUnit}.
     *
     * @param context the context represent the associated {@link VirtualProcessorUnit}.
     */
    void init( @Nonnull Context context );

    /**
     * Queue task for execution and enable the executor for activation if necessary.
     * The task must not be already queued.
     *
     * @param task the task.
     */
    void queue( @Nonnull Task task );
  }

  @FunctionalInterface
  interface ActivationFn
  {
    /**
     * Callback method invoked by {@link Context#activate(ActivationFn)} to process tasks.
     */
    void invoke();
  }

  /**
   * Interface representing {@link VirtualProcessorUnit} passed to {@link Executor} during initialization.
   * This interface is designed to allow the {@link Executor} to activate the {@link VirtualProcessorUnit}
   * when it needs to execute tasks.
   */
  interface Context
  {
    /**
     * Activate the associated {@link VirtualProcessorUnit}.
     * This method MUST only be called if there is no {@link VirtualProcessorUnit} unit currently activated.
     * The activation will set the {@link VirtualProcessorUnit#current()} for the duration
     * of the activation and invoke {@link ActivationFn#invoke()} passed into the method.
     *
     * @param activationFn the function passed to process tasks.
     */
    void activate( @Nonnull ActivationFn activationFn );
  }
}
