/*Gets a frame of 16bit data*/
public interface FrameSequence {

  public Short getFrame() throws InterruptedException;
  
}
