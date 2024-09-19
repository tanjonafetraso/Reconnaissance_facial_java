/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recognition;



import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class Enregistrement extends javax.swing.JFrame {

    private static boolean captureRequested = false;
    private BufferedImage image;
    private OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat();
    private OpenCVFrameGrabber camera1 = new OpenCVFrameGrabber(0);
    private opencv_objdetect.CascadeClassifier faceDetector;

    private void startCapture() {
        new SwingWorker<Void, BufferedImage>() {
            @Override
            protected Void doInBackground() throws Exception {
                Frame capturedFrame;
                opencv_core.Mat colorImage = new opencv_core.Mat();
                opencv_core.Mat grayImage = new opencv_core.Mat();
                opencv_core.RectVector detectedFaces = new opencv_core.RectVector();

                while (true) {
                    if ((capturedFrame = camera1.grab()) != null) {
                        colorImage = convertMat.convert(capturedFrame);
                        opencv_imgproc.cvtColor(colorImage, grayImage, opencv_imgproc.COLOR_BGR2GRAY);
                        faceDetector.detectMultiScale(grayImage, detectedFaces, 1.1, 1, 0, new opencv_core.Size(150, 150), new opencv_core.Size(500, 500));

                        for (int i = 0; i < detectedFaces.size(); i++) {
                            opencv_core.Rect faceData = detectedFaces.get(i);
                            opencv_imgproc.rectangle(colorImage, faceData, new opencv_core.Scalar(0, 0, 255, 0));
                            opencv_core.Mat capturedFace = new opencv_core.Mat(grayImage, faceData);
                            opencv_imgproc.resize(capturedFace, capturedFace, new opencv_core.Size(160, 160));

                            if (captureRequested) {
                                int personId = 1; // Replace with actual person ID logic
                                String fileName = "src/photos/person." + personId + ".jpg";
                                opencv_imgcodecs.imwrite(fileName, capturedFace);
                                System.out.println("Photo captured: " + fileName);
                                captureRequested = false;
                            }
                        }

                        BufferedImage img = new Java2DFrameConverter().convert(capturedFrame);
                        publish(img);
                    }
                }
            }

            @Override
            protected void process(java.util.List<BufferedImage> chunks) {
                updateImage(chunks.get(chunks.size() - 1));
            }
        }.execute();
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        // Dessiner sur le JPanel
        if (image != null) {
            g.drawImage(image, 0, 0, jPanel1.getWidth(), jPanel1.getHeight(), null);
        }
    }

    public void updateImage(BufferedImage img) {
        this.image = img;
        repaint();
    }

    public Enregistrement() {
        initComponents();
        try {
            camera1.start();
            faceDetector = new opencv_objdetect.CascadeClassifier("src/resources/haarcascade_frontalface_alt.xml");
            startCapture();
        } catch (Exception e) {
            Logger.getLogger(Enregistrement.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void initComponents() {
        jPanel1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        captureButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 116, Short.MAX_VALUE)
        );

        captureButton.setText("Capture");
        captureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                captureButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(250, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(captureButton)
                .addGap(91, 91, 91))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(captureButton)
                .addContainerGap(103, Short.MAX_VALUE))
        );

        pack();
    }

    private void captureButtonActionPerformed(java.awt.event.ActionEvent evt) {
        captureRequested = true;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Enregistrement().setVisible(true);
            }
        });
    }

    private javax.swing.JButton captureButton;
    private javax.swing.JPanel jPanel1;
}
