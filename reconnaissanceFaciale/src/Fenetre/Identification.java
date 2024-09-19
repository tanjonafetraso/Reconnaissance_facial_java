/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Fenetre;

import DAO.HibernateDAO;
import Model.Identite;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 *
 * @author TANJONA
 */
public class Identification extends javax.swing.JFrame {

//    private String[] people = {"", "Tanjona", "Agnes", "Priscila"};
    String[] identiteData = new String[26];
    boolean isVerifier = true;
    private List<Identite> people = new ArrayList<>();
    private int sampleNumber = 30;
    private int sample = 1;
    private Long lastIdPersonne;
    private HibernateDAO dao;
    private static boolean captureRequested = false;
    private BufferedImage image;
    private OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat();
    private OpenCVFrameGrabber camera1 = new OpenCVFrameGrabber(0);
    private opencv_objdetect.CascadeClassifier faceDetector;
    FaceRecognizer recognizer = createLBPHFaceRecognizer();
    String resultatsAffichier = "";
    int thickness = 10, lineType = 8, shift = 0;
    Scalar color = new Scalar(0, 255, 0, 0); // Vert pur

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

                        for (int i = 0; i < detectedFaces.size(); i++) { // cycle detected faces vector
                            Rect faceData = detectedFaces.get(i);
                             opencv_imgproc.rectangle(colorImage, faceData, color, thickness, lineType, shift);
                            // image
                            Mat capturedface = new Mat(grayImage, faceData);
                            opencv_imgproc.resize(capturedface, capturedface, new Size(160, 160));

                            IntPointer label = new IntPointer(1); // identify the image label
                            DoublePointer confidence = new DoublePointer(1);
                            recognizer.predict(capturedface, label, confidence); // will classify the new image according to the training
                            int selection = label.get(0); // choice made by the classifier
                            String name = "";
                            if (isVerifier == true) {

                                if (selection == -1) {
                                    name = "Inconnue";
                                } else {
                                    int res = selection - 1;
                                    Identite p = people.get(res);
                                    name = p.getNom();
                                    System.out.println(res + "  -------  " + people.get(res).getAdresseEmail() + "--------------" + people.get(res).getIdPersonne());
                                    isVerifier = false;
                                    btn_ok.setEnabled(true);
                                    btn_stop.setEnabled(false);
                                    stopCamera();
                                    identiteData[0] = "Nom : " + p.getNom();
                                    identiteData[1] = "Prénom : " + p.getPrenom();
                                    identiteData[2] = "Date de naissance : " + p.getDateNaissance();
                                    identiteData[3] = "Lieu de naissance : " + p.getLieuNaissance();
                                    identiteData[4] = "Nationalité : " + p.getNationalite();
                                    identiteData[5] = "sexe : " + p.getSexe();

//                                    String[] aspectPhysiqueData = new String[5];
                                    identiteData[6] = "Taille : " + p.getTaille();
                                    identiteData[7] = "Poids : " + p.getPaoids();
                                    identiteData[8] = "Couleur des yeux : " + p.getCouleurYeux();
                                    identiteData[9] = "Couleur des cheveux : " + p.getCouleurCheveux();
                                    identiteData[10] = "Signes particuliers : " + p.getSignesParticuliere();
//                                    jList7.setListData(aspectPhysiqueData);
//                                    String[] historiqueData = new String[5];
                                    identiteData[11] = "Infractions : " + p.getTaille();
                                    identiteData[12] = "Dates des infractions : " + p.getPaoids();
                                    identiteData[13] = "Lieux des infractions : " + p.getCouleurYeux();
                                    identiteData[14] = "Condamnations : " + p.getCouleurCheveux();
                                    identiteData[15] = "Antécédents judiciaires : " + p.getSignesParticuliere();
//                                    jList4.setListData(historiqueData);
//                                    String[] contexteData = new String[3];
                                    identiteData[16] = "Situation familiale : " + p.getSituationFamiliale();
                                    identiteData[17] = "Niveau d’éducation : " + p.getNiveauEducation();
                                    identiteData[18] = "Profession actuelle : " + p.getProfessionActuelle();
//                                    ListeContexte.setListData(contexteData);
//                                    String[] InformationsData = new String[3];
                                    identiteData[19] = "Mode opératoire: " + p.getModeOperatoire();
                                    identiteData[20] = "Associés ou complices : " + p.getAssocierOuComplice();
                                    identiteData[21] = "Comportements particuliers : " + p.getComportementParticuliere();
//                                    jList5.setListData(InformationsData);
//                                    String[] coordonnesData = new String[4];
                                    identiteData[22] = "Adresse actuelle : " + p.getAdresseActuelle();
                                    identiteData[23] = "Adresse précédente : " + p.getAdressePrecedent();
                                    identiteData[24] = "Numéro de téléphone : " + p.getNumeroTelephone();
                                    identiteData[25] = "Adresse e-mail : " + p.getAdresseEmail();
//                                    listeCoordonnees.setListData(coordonnesData);
                                    ListeIdentite.setListData(identiteData);

                                }
                                resultatsAffichier = name;
                            }
                            resultat.setText(resultatsAffichier);
                            // entering the name on the screen
                            int x = Math.max(faceData.tl().x() - 10, 0);
                            int y = Math.max(faceData.tl().y() - 10, 0);
                            opencv_imgproc.putText(colorImage, name, new Point(x, y), opencv_core.FONT_HERSHEY_PLAIN, 1.4, new Scalar(0, 255, 0, 0));

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

    /**
     * Creates new form Enregistrement
     */
    public Identification() {

        initComponents();
        btn_ok.setEnabled(false);
        btn_stop.setEnabled(false);
        recognizer.load("src\\resources\\classifierLBPH.yml");
        recognizer.setThreshold(65.0);
        dao = new HibernateDAO();
        people = dao.findAll(new Identite());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_ok = new javax.swing.JButton();
        jPanel1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        btn_play = new javax.swing.JButton();
        btn_stop = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ListeIdentite = new javax.swing.JList<>();
        jButton4 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        imagePanel = new javax.swing.JPanel();
        resultat = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Identification faciale\n");
        setIconImages(null);

        btn_ok.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_ok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Fond/check-7050.png"))); // NOI18N
        btn_ok.setText("OK");
        btn_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_okActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 245, Short.MAX_VALUE)
        );

        btn_play.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Fond/video-camera-recording-13723.png"))); // NOI18N
        btn_play.setText("play");
        btn_play.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_playActionPerformed(evt);
            }
        });

        btn_stop.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Fond/no-video-13738.png"))); // NOI18N
        btn_stop.setText("stop");
        btn_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_stopActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("FICHE DE RENSEIGNEMENTS CRIMINELS");

        ListeIdentite.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "INDENTITE", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N
        ListeIdentite.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        ListeIdentite.setOpaque(false);
        jScrollPane2.setViewportView(ListeIdentite);

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Fond/left-arrow-5737.png"))); // NOI18N
        jButton4.setText("Retour");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 7, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(0, 17, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Fond/images.jpg"))); // NOI18N
        jLabel3.setText("jLabel3");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Fond/Capture d’écran 2024-09-16 150003.jpg"))); // NOI18N
        jLabel4.setText("jLabel3");

        imagePanel.setBackground(new java.awt.Color(0, 153, 153));

        resultat.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        resultat.setForeground(new java.awt.Color(255, 255, 255));
        resultat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resultat.setText("Inconnue");

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imagePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultat, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(resultat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(btn_play)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btn_stop))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(116, 116, 116)
                                .addComponent(btn_ok))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(imagePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 399, Short.MAX_VALUE))
                .addGap(1, 1, 1)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_play)
                    .addComponent(btn_stop)
                    .addComponent(btn_ok))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_okActionPerformed

        resultat.setText("");
        isVerifier = true;
        btn_stop.setEnabled(true);
        btn_ok.setEnabled(false);
        stratCamera();
        for (int i = 0; i < 26; i++) {
            identiteData[i] = "";
        }
        ListeIdentite.setListData(identiteData);
    }//GEN-LAST:event_btn_okActionPerformed

    private void btn_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_stopActionPerformed
        try {
            stopCamera();

            btn_play.setEnabled(true);
            //    btn_stop.setEnabled(false);
        } catch (InterruptedException ex) {
            Logger.getLogger(Identification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_stopActionPerformed

    private void btn_playActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_playActionPerformed
        stratCamera();
        btn_play.setEnabled(false);
        btn_stop.setEnabled(true);// TODO add your handling code here:
    }//GEN-LAST:event_btn_playActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            stopCamera();
            this.setVisible(false);
            Menu m = new Menu();
            m.setVisible(true);
// TODO add your handling code here:
        } catch (InterruptedException ex) {
            Logger.getLogger(Identification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed
    public void stratCamera() {
        try {
            camera1.start();
            faceDetector = new opencv_objdetect.CascadeClassifier("src/resources/haarcascade_frontalface_alt.xml");
            startCapture();

        } catch (Exception e) {
            Logger.getLogger(recognition.Enregistrement.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void stopCamera() throws InterruptedException {
        try {
            camera1.stop();
            this.image = null;
            // Forcer la mise à jour sur le thread de l'interface graphique
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    jPanel1.repaint(); // Redessiner le panneau
                }
            });
            // TODO add your handling code here:
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Identification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Identification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Identification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Identification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Identification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Identification().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> ListeIdentite;
    private javax.swing.JButton btn_ok;
    private javax.swing.JButton btn_play;
    private javax.swing.JButton btn_stop;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel resultat;
    // End of variables declaration//GEN-END:variables
}
