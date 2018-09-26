package streak.schedulers.m1;

import org.testng.annotations.Test;
import streak.AbstractStreakTest;
import static org.testng.Assert.*;

public class CircularBufferTest
  extends AbstractStreakTest
{
  @Test
  public void basicOperation()
  {
    final CircularBuffer<String> buffer = new CircularBuffer<>( 3 );
    assertEquals( buffer.size(), 0 );
    assertNull( buffer.get( 0 ) );
    assertNull( buffer.get( 1 ) );
    assertNull( buffer.get( 2 ) );
    // The following gets exceed the buffers capacity but should be gracefully handled
    assertNull( buffer.get( 3 ) );
    assertNull( buffer.get( 4 ) );

    buffer.add( "A" );
    assertEquals( buffer.size(), 1 );
    assertEquals( buffer.get( 0 ), "A" );
    assertNull( buffer.get( 1 ) );
    assertNull( buffer.get( 2 ) );
    assertNull( buffer.get( 3 ) );
    assertNull( buffer.get( 4 ) );

    buffer.add( "B" );
    assertEquals( buffer.size(), 2 );
    assertEquals( buffer.get( 0 ), "A" );
    assertEquals( buffer.get( 1 ), "B" );
    assertNull( buffer.get( 2 ) );
    assertNull( buffer.get( 3 ) );
    assertNull( buffer.get( 4 ) );

    buffer.add( "C" );
    assertEquals( buffer.size(), 3 );
    assertEquals( buffer.get( 0 ), "A" );
    assertEquals( buffer.get( 1 ), "B" );
    assertEquals( buffer.get( 2 ), "C" );
    assertNull( buffer.get( 3 ) );
    assertNull( buffer.get( 4 ) );

    assertEquals( buffer.pop(), "A" );

    assertEquals( buffer.size(), 2 );
    assertEquals( buffer.get( 0 ), "B" );
    assertEquals( buffer.get( 1 ), "C" );
    assertNull( buffer.get( 2 ) );
    assertNull( buffer.get( 3 ) );
    assertNull( buffer.get( 4 ) );

    buffer.add( "D" );
    assertEquals( buffer.size(), 3 );
    assertEquals( buffer.get( 0 ), "B" );
    assertEquals( buffer.get( 1 ), "C" );
    assertEquals( buffer.get( 2 ), "D" );
    assertNull( buffer.get( 3 ) );
    assertNull( buffer.get( 4 ) );

    buffer.add( "E" );
    buffer.add( "F" );
    buffer.add( "G" );
    assertEquals( buffer.size(), 6 );
    assertEquals( buffer.get( 0 ), "B" );
    assertEquals( buffer.get( 1 ), "C" );
    assertEquals( buffer.get( 2 ), "D" );
    assertEquals( buffer.get( 3 ), "E" );
    assertEquals( buffer.get( 4 ), "F" );
    assertEquals( buffer.get( 5 ), "G" );

    assertEquals( buffer.pop(), "B" );
    assertEquals( buffer.pop(), "C" );
    assertEquals( buffer.pop(), "D" );
    assertEquals( buffer.pop(), "E" );
    assertEquals( buffer.pop(), "F" );
    assertEquals( buffer.pop(), "G" );
    assertNull( buffer.pop() );
  }

  @Test
  public void popAfterWrapping()
  {
    final CircularBuffer<String> buffer = new CircularBuffer<>( 3 );
    assertEquals( buffer.size(), 0 );

    buffer.add( "A" );
    assertEquals( buffer.size(), 1 );
    assertEquals( buffer.get( 0 ), "A" );

    buffer.add( "B" );
    assertEquals( buffer.size(), 2 );
    assertEquals( buffer.get( 0 ), "A" );
    assertEquals( buffer.get( 1 ), "B" );

    buffer.add( "C" );
    assertEquals( buffer.size(), 3 );
    assertEquals( buffer.get( 0 ), "A" );
    assertEquals( buffer.get( 1 ), "B" );
    assertEquals( buffer.get( 2 ), "C" );

    assertEquals( buffer.pop(), "A" );
    assertEquals( buffer.pop(), "B" );

    assertEquals( buffer.size(), 1 );
    assertEquals( buffer.get( 0 ), "C" );

    buffer.add( "D" );
    assertEquals( buffer.size(), 2 );
    assertEquals( buffer.get( 0 ), "C" );
    assertEquals( buffer.get( 1 ), "D" );

    buffer.add( "E" );

    assertEquals( buffer.pop(), "C" );
    assertEquals( buffer.pop(), "D" );
    assertEquals( buffer.pop(), "E" );
  }

  @Test
  public void truncateAfterWrapping()
  {
    final CircularBuffer<String> buffer = new CircularBuffer<>( 3 );
    assertEquals( buffer.size(), 0 );

    buffer.add( "A" );
    buffer.add( "B" );
    buffer.add( "C" );

    assertEquals( buffer.pop(), "A" );
    assertEquals( buffer.pop(), "B" );

    assertEquals( buffer.size(), 1 );

    buffer.add( "D" );
    buffer.add( "E" );

    buffer.truncate( 2 );

    assertEquals( buffer.pop(), "C" );
    assertEquals( buffer.pop(), "D" );
    assertNull( buffer.pop() );
  }

  @Test
  public void addFirst()
  {
    final CircularBuffer<String> buffer = new CircularBuffer<>( 3 );
    assertEquals( buffer.size(), 0 );

    buffer.add( "A" );
    buffer.add( "B" );
    buffer.add( "C" );
    // This triggers both a grow and a wrap
    buffer.addFirst( "D" );

    assertEquals( buffer.size(), 4 );

    assertEquals( buffer.pop(), "D" );
    assertEquals( buffer.pop(), "A" );
    assertEquals( buffer.pop(), "B" );
    assertEquals( buffer.pop(), "C" );

    buffer.addFirst( "E" );
    buffer.addFirst( "F" );
    buffer.add( "G" );

    assertEquals( buffer.pop(), "F" );
    assertEquals( buffer.pop(), "E" );
    assertEquals( buffer.pop(), "G" );
  }
}