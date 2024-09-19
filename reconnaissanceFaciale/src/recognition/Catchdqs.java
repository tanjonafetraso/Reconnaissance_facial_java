/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recognition;



import Fenetre.Enregistrement;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

public class Catchdqs {

    private static boolean captureRequested = false;

    public static void main(String[] args) throws Exception, InterruptedException {
        OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat(); // Convert image to Mat
        OpenCVFrameGrabber camera1 = new OpenCVFrameGrabber(0); // Capturing webcam images
        camera1.start();

        // Create a new instance of the Enregistrement form
        Enregistrement form = new Enregistrement();
        form.setLayout(new BorderLayout());

        // Panel for displaying webcam feed
        WebcamPanel webcamPanel = new WebcamPanel();
        form.add(webcamPanel, BorderLayout.CENTER);

        // Button to capture photos
        JButton captureButton = new JButton("Capture Photo");
        form.add(captureButton, BorderLayout.SOUTH);
        form.setVisible(true);

        CascadeClassifier faceDetector = new CascadeClassifier("src\\resources\\haarcascade_frontalface_alt.xml");

        Frame capturedFrame;
        Mat colorImage = new Mat(); // To store the color image
        int sampleNumber = 30;
        int sample = 1;

        // Listener for the capture button
        captureButton.addActionListener(e -> captureRequested = true); // Set flag to true when button is pressed

        // Request user ID
        System.out.println("Enter your ID:");
        java.util.Scanner register = new java.util.Scanner(System.in);
        int personId = register.nextInt();

        while ((capturedFrame = camera1.grab()) != null) {
            colorImage = convertMat.convert(capturedFrame);
            Mat grayImage = new Mat();
            opencv_imgproc.cvtColor(colorImage, grayImage, opencv_imgproc.COLOR_BGRA2GRAY); // Convert image to grayscale
            RectVector detectedFaces = new RectVector(); // Store detected faces
            faceDetector.detectMultiScale(grayImage, detectedFaces, 1.1, 1, 0, new Size(150, 150), new Size(500, 500));

            for (int i = 0; i < detectedFaces.size(); i++) {
                Rect faceData = detectedFaces.get(0);
                opencv_imgproc.rectangle(colorImage, faceData, new Scalar(0, 0, 255, 0)); // Draw rectangle on face
                Mat capturedFace = new Mat(grayImage, faceData);
                opencv_imgproc.resize(capturedFace, capturedFace, new Size(160, 160)); // Resize face image

                // Capture face when button is clicked
                if (captureRequested && sample <= sampleNumber) {
                    opencv_imgcodecs.imwrite("src\\photos\\person." + personId + "." + sample + ".jpg", capturedFace);
                    System.out.println("Photo " + sample + " captured\n");
                    sample++;
                    captureRequested = false; // Reset capture flag after capturing
                }
            }

            // Update the JPanel with the webcam feed
            BufferedImage img = new Java2DFrameConverter().convert(capturedFrame);
            webcamPanel.updateImage(img);

            if (sample > sampleNumber) {
                break; // Exit loop when enough samples have been taken
            }
        }

        // Cleanup resources
        camera1.stop();
        camera1.close();
        colorImage.close();
        register.close();
        faceDetector.close();
    }

    // Panel for displaying the webcam feed
    static class WebcamPanel extends JPanel {
        private BufferedImage image;

        public void updateImage(BufferedImage img) {
            this.image = img;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
            }
        }
    }
}
