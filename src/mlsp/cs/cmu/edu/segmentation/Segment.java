package mlsp.cs.cmu.edu.segmentation;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import mlsp.cs.cmu.edu.audio.AudioConstants;

/* Iterator behavior is for feature extraction. Yields frames of 25 ms each*/
public class Segment implements Iterator<Short[]>, Cloneable {
  
  private AudioFormat audioFormat;

  private ArrayList<Short> waveform;

  private ArrayList<Double> decibelWaveform;

  private ArrayList<Double[]> waveframes;
  
  private Integer startFrame = 0;
  
  private Integer endFrame = 0;
  
  private Integer frameSize = 0;
  
  private ArrayList<Short> flattenedWavFrames;
  
  private LinkedBlockingQueue<Short[]> iterableWaveform;
  
  private boolean startedIteration = false;
  
  public Segment(AudioFormat audioFormat, ArrayList<Short> waveform,
          ArrayList<Double> decibelWaveform, ArrayList<Double[]> waveframes) {
    this.audioFormat = audioFormat;
    this.waveform = waveform;
    this.decibelWaveform = decibelWaveform;
    this.waveframes = waveframes;
    this.startFrame = 0;
    this.endFrame = waveframes.size()-1; // defaults to size of array
    this.frameSize = waveframes.get(0).length;
    this.iterableWaveform = new LinkedBlockingQueue<Short[]>();
  }
  
  // make a clone of this thing!
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      System.out.println("Could not clone object!");
      e.printStackTrace();
    }
    return null;
  }
  
  private LinkedBlockingQueue<Short[]> buildIterableWaveform() throws InterruptedException {
    ArrayList<Short> wav = getProcessedWaveform();
    LinkedBlockingQueue<Short[]> queue = new LinkedBlockingQueue<Short[]>();
    Integer samplesPerMillisecond = (int) audioFormat.getSampleRate() / 1000;
    Integer windowSize = AudioConstants.FRAMESIZE.getValue() * samplesPerMillisecond;
    Integer frameShift = AudioConstants.FRAMESHIFT.getValue() * samplesPerMillisecond;
    for(int i = 0; i < (wav.size()-windowSize-frameShift); i += frameShift) {
      Short[] frame = new Short[windowSize];
      for(int j = i; j < frame.length; j++) {
        frame[j-i] = wav.get(i);
      }
      queue.put(frame);
    }
    return queue;
  }

  public void setEndpoints(Integer start, Integer end) {
    this.startFrame = start;
    this.endFrame = end;
  }
  
  public AudioInputStream getAudioStream() {
    ArrayList<Short> wav = getProcessedWaveform();
    ByteArrayInputStream buff = new ByteArrayInputStream(convertShortsToBytes(wav));
    return new AudioInputStream(buff, audioFormat, (wav.size()*2));
  }

  public Double getFrameEnergy(Integer frameIndex) {
    return decibelWaveform.get(frameIndex);
  }
  
  @Override
  public boolean hasNext() {
    return iterableWaveform.isEmpty();
  }

  @Override
  public Short[] next() {
    try {
      if(!startedIteration) {
        iterableWaveform = buildIterableWaveform();
        startedIteration = true;
      }
      return iterableWaveform.take();
    } catch (InterruptedException e) {
      System.out.println("Unable to retrieve next frame!");
      e.printStackTrace();
    }
    return null;
  }
  
  public ArrayList<Short> getWaveform() {
    return getWaveformSegmentSubset(waveform);
  }
  
  public ArrayList<Short> getProcessedWaveform() {
    return getWaveformSegmentSubset(getFlattenedWavFrames());
  }
  
 
  
  private ArrayList<Short> getFlattenedWavFrames() {
    flattenedWavFrames = new ArrayList<Short>();
    for(Double[] frame : waveframes) {
      for(Double sample : frame) {
        flattenedWavFrames.add(sample.shortValue());
      }
    }
    return flattenedWavFrames;
  }
  
  private byte[] convertShortsToBytes(ArrayList<Short> arr) {
    byte[] byteArray = new byte[arr.size() * 2];
    Integer i = 0;
    for(Short s : arr) {
      ByteBuffer bb = ByteBuffer.allocate(2);
      bb.order(java.nio.ByteOrder.nativeOrder());
      bb.putShort(s);
      byteArray[i++] = bb.get(0);
      byteArray[i++] = bb.get(1);
    }
    return byteArray;
  }
  
  private Integer getStartSample() {
    return Math.max(((startFrame-1) * frameSize), 0);
  }
  
  private Integer getEndSample() {
    return Math.min(((endFrame-1) * frameSize), waveform.size()-1);
  }
  
  private ArrayList<Short> getWaveformSegmentSubset(ArrayList<Short> arr) {
    Integer startIndex = getStartSample();
    Integer endIndex = getEndSample();
    List<Short> returnList = arr.subList(startIndex, endIndex);
    return new ArrayList<Short>(returnList);
  }

  public Double getStartFrameTimestamp() {
    return getStartSample().doubleValue() / audioFormat.getSampleRate();
  }

  public Double getEndFrameTimestamp() {
    return getEndSample().doubleValue() / audioFormat.getSampleRate();
  }
  
}
