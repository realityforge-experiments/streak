package spritz.internal.vpu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import spritz.schedulers.CircularBuffer;
import static org.realityforge.braincheck.Guards.*;

/**
 * A very simple first-in first out task queue.
 */
public final class FifoTaskQueue
  implements TaskQueue
{
  /**
   * A buffer per priority containing tasks that have been scheduled but are not executing.
   */
  @Nonnull
  private final CircularBuffer<Task> _buffer;

  public FifoTaskQueue( final int initialCapacity )
  {
    _buffer = new CircularBuffer<>( initialCapacity );
  }

  @Override
  public int getQueueSize()
  {
    return _buffer.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasTasks()
  {
    return !_buffer.isEmpty();
  }

  /**
   * Add the specified task to the queue.
   * The task must not already be in the queue.
   *
   * @param task the task.
   */
  @Override
  public void queueTask( @Nonnull final Task task )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> !_buffer.contains( task ),
                 () -> "Spritz-0098: Attempting to queue task " + task + " when task is already queued." );
    }
    Objects.requireNonNull( task ).markAsQueued();
    _buffer.add( Objects.requireNonNull( task ) );
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public Task dequeueTask()
  {
    return _buffer.pop();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Task> clear()
  {
    final ArrayList<Task> tasks = new ArrayList<>();
    Task task;
    while ( null != ( task = _buffer.pop() ) )
    {
      tasks.add( task );
      task.markAsIdle();
    }
    return tasks;
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public Stream<Task> getOrderedTasks()
  {
    return _buffer.stream();
  }

  @Nonnull
  CircularBuffer<Task> getBuffer()
  {
    return _buffer;
  }
}