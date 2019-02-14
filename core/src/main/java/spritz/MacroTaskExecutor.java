package spritz;

/**
 * Run tasks in next MacroTask.
 */
final class MacroTaskExecutor
  extends RoundBasedExecutor
{
  /**
   * {@inheritDoc}
   */
  @Override
  protected final void scheduleForActivation()
  {
    Scheduler.schedule( () -> context().activate( this::executeTasks ), 0 );
  }
}
