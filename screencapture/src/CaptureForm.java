

import eduze.livestream.Multiplexer;
import eduze.livestream.ScreenCapturer;
import eduze.livestream.ScreenReceiver;
import eduze.livestream.exchange.Connector;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.rpc.ServiceException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import eduze.livestream.exchange.client.FrameBuffer;

/**
 * Created by Fujitsu on 3/28/2016.
 */
public class CaptureForm extends JFrame {
    private JPanel jPanel1;
    private JButton button1;
    private JLabel jImageLabel;
    private JButton btnSwitch;
    ScreenCapturer cap;
    ScreenCapturer cap2;
    ScreenReceiver recv;

    private boolean channel1 = false;
    Multiplexer multi;
    public CaptureForm()
    {
        /*FrameBufferImplService service2 = null;
        FrameBufferImplService service = null;
        FrameBufferImplService serviceOutput = null;
        try {
            service = new FrameBufferImplService(new URL("http://localhost:8000/screenrelay?wsdl"));
            service2 = new FrameBufferImplService(new URL("http://localhost:8000/screenrelay2?wsdl"));
            serviceOutput = new FrameBufferImplService(new URL("http://localhost:8000/screenrelayoutput?wsdl"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        FrameBuffer outputBuffer = null;
        FrameBuffer frameBuffer2 = null;
        FrameBuffer frameBuffer = null;
        try {
            multi = new Multiplexer(new URL("http://localhost:8000/screenrelayoutput?wsdl"));
            outputBuffer = Connector.obtainFrameBuffer("http://localhost:8000/screenrelayoutput?wsdl");
            frameBuffer = Connector.obtainFrameBuffer("http://localhost:8000/screenrelay?wsdl");
            frameBuffer2 = Connector.obtainFrameBuffer("http://localhost:8000/screenrelay2?wsdl");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        cap = new ScreenCapturer(frameBuffer,5000);
        cap2 = new ScreenCapturer(frameBuffer2,100);

        recv = new ScreenReceiver(outputBuffer,1);

        recv.addScreenReceivedListener(new ScreenReceiver.ScreenReceivedListener() {
            @Override
            public void ScreenReceived(byte[] screen, BufferedImage screenImage) {
                jImageLabel.setIcon(new ImageIcon(screenImage));
            }
        });

        recv.startReceiving();

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(cap2.isCapturing() )
                {
                    cap.stopCapture();
                    cap2.stopCapture();
                }
                else {
                    try {
                        cap.startCapture();
                        cap2.startCapture();
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    multi.start();
                }
            }
        });

        btnSwitch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(channel1)
                {
                    channel1 = false;
                    try {
                        setTitle("Channel 2");
                        multi.setInputURL(new URL("http://localhost:8000/screenrelay2?wsdl"));
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                }
                else
                {
                    channel1 = true;
                    try {
                        setTitle("Channel 1");
                        multi.setInputURL(new URL("http://localhost:8000/screenrelay1?wsdl"));
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });

        jImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                btnSwitch.doClick();
            }
        });

    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("CaptureForm");
        frame.setContentPane(new CaptureForm().jPanel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,600);
        frame.setVisible(true);


    }

    private Thread ScreenCaptureThread = null;
    public void StartScreenCapture()
    {
        ScreenCaptureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                BufferedImage image = null;
                                try {
                                    image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                                } catch (AWTException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    ImageIO.write(image, "png", new File("screenshot.png"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        ScreenCaptureThread.start();
    }

}
