package pl.patrykheciak.apkadodatkowa;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private Paint paint = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);

        paint.setColor(Color.MAGENTA);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = editText.getText().toString();
                String[] numbersAsStringAr = text.split(",");

                if (numbersAsStringAr.length < 4 || numbersAsStringAr.length > 7) {
                    Toast.makeText(MainActivity.this, "Nieprawidłowa ilość liczb", Toast.LENGTH_LONG).show();
                }

                int sum = 0;
                int[] numbers = new int[numbersAsStringAr.length];
                for (int i = 0; i < numbers.length; i++) {

                    try {
                        numbers[i] = Integer.parseInt(numbersAsStringAr[i]);
                        sum += numbers[i];
                    } catch (NumberFormatException e) {

                    }
                }

                double[] percents = new double[numbersAsStringAr.length];
                for (int i = 0; i < numbers.length; i++) {
                    percents[i] = (double) numbers[i] / (double) sum;
                    if (i > 0)
                        percents[i] += percents[i - 1];
                    Log.d("MYTAG", "percent " + i + ": " + percents[i]);
                }

                drawChart(percents, holder);
            }
        });

        surfaceView = findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
    }

    private void drawChart(double[] percents, SurfaceHolder holder) {

        int[] colors = new int[percents.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.rgb(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255));
        }

        Canvas canvas = null;

        try {
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);

            int width = holder.getSurfaceFrame().width();
            int height = holder.getSurfaceFrame().height();
            int xc = width / 2;
            int yc = height / 2;

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(width, height, conf);

            Log.d("MYTAG", "width " + width + ", height " + height);

            for (int x = 0; x < width; x++) {
                Log.d("MYTAG", "x " + x);
                for (int y = 0; y < height; y++) {
                    if ((x - xc) * (x - xc) + (y - yc) * (y - yc) < 160000) {
                        // x,y lies within a circle
                        double v = Math.atan2(yc - y, xc - x);
                        double vdivPi = v / Math.PI;
                        vdivPi /= 2;
                        vdivPi += 0.5;
                        for (int i = 0; i < percents.length; i++) {
                            if (i != percents.length - 1) {
                                if (percents[i] < vdivPi && vdivPi < percents[(i + 1) % percents.length]) {
                                    bmp.setPixel(x, y, colors[i]);
                                }
                            } else {
                                if (vdivPi < percents[0]) {
                                    bmp.setPixel(x, y, colors[i]);
                                }
                            }

                        }

                    }
                }
            }

            synchronized (holder) {

                canvas.drawBitmap(bmp, 0, 0, null);

            }
        } finally {
            // in case of an exception the surface is not left in an inconsistent state
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }
}
