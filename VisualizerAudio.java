
public class MainActivity extends AppCompatActivity {

   
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    }

    private void link() {
        if (mp == null) {
            throw new NullPointerException("Cannot link to null Media Player");
        }

        try {
            final Visualizer visualizer;
           visualizer = new Visualizer(mp.getAudioSessionId());

            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);

            Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer,
                                                  byte[] buf
                        , int samplingRate) {
                  
                    float[] samples = new float[buf.length / 2];
               
                    float lastPeak = 0f;
               
                    for (int i = 0, s = 0; i < buf.length - 1; ) {
                        int sample = 0;
                        sample |= buf[i++] & 0xFF;
                        sample |= buf[i++] << 8;
                        // normalize to range +/-1.0f
                        samples[s++] = sample / 32768f;
                    }

                      float peak = 0f;

                     for (float sample : samples) {
                        peak = Math.max(Math.abs(sample), peak);
                    }
     if (lastPeak > peak) peak = lastPeak * 0.875f;

                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] waveFormData, int i) {
                }
            };
            visualizer.setDataCaptureListener(
                    captureListener, Visualizer.getMaxCaptureRate() / 2, true, false);

            visualizer.setEnabled(true);

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    visualizer.setEnabled(false);
                    ivPerson.setImageResource(R.drawable.b_group);
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}