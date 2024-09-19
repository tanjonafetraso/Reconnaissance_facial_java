/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recognition;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class Catchs {
    private static boolean captureRequested = false;

    public static void main(String[] args) throws Exception, InterruptedException {
        OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat(); // convert image to Mat
        OpenCVFrameGrabber camera1 = new OpenCVFrameGrabber(0); // capturing webcam images

        camera1.start();

        CascadeClassifier faceDetector = new CascadeClassifier("src\\resources\\haarcascade_frontalface_alt.xml");

        // Create a JFrame to hold the button and canvas frame
        JFrame frame = new JFrame("Face Capture");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Create a JPanel to hold the button
        JPanel panel = new JPanel();
        JButton captureButton = new JButton("Capture Photo");
        panel.add(captureButton);
        frame.add(panel, BorderLayout.SOUTH);

        // CanvasFrame for webcam preview
        CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera1.getGamma());
        frame.add(cFrame.getContentPane(), BorderLayout.CENTER);

        // Display the JFrame
        frame.setVisible(true);

        Frame capturedFrame = null;
        Mat colorImage = new Mat(); // to store the color image
        int sampleNumber = 30;
        int sample = 1;

        // Listener for the capture button
        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                captureRequested = true; // Set flag to true when button is pressed
            }
        });

        // Request user ID
        System.out.println("Enter your ID:");
        Scanner register = new Scanner(System.in);
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
                    opencv_imgcodecs.imwrite("C:\\Users\\TANJONA\\Documents\\NetBeansProjects\\reconnaissanceFaciale\\src\\photos\\person." + personId + "." + sample + ".jpg", capturedFace);
                    System.out.println("Photo " + sample + " captured\n");
                    sample++;
                    captureRequested = false; // Reset capture flag after capturing
                }
            }

            if (cFrame.isVisible()) {
                cFrame.showImage(capturedFrame);
            }

            if (sample > sampleNumber) {
                break; // Exit loop when enough samples have been taken
            }
        }

        // Cleanup resources
        cFrame.dispose();
        camera1.stop();
        camera1.close();
        colorImage.close();
        register.close();
        faceDetector.close();
    }
}
