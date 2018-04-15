/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author stark
 */
public class Slide {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Slide slide = new Slide();
    }

    JButton btnExit;
    JFrame frame;
    final JFileChooser fc = new JFileChooser();
    HeyListen hl;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    JLabel img;
    JLabel debug;

    Slide() {
        getProps();
        frame = new JFrame();//creating instance of JFrame  
        hl = new HeyListen(this, frame);
        frame.setName("main frame");

        JButton b = new JButton("click");//creating instance of JButton 
        b.setName("why");
        b.setBounds(0, 0, 0, 0);//x axis, y axis, width, height  
        b.addActionListener((java.awt.event.ActionEvent e) -> {
            setImg();
        });
        frame.add(b);//adding button in JFrame  

        debug = new JLabel();
        debug.setText(hl.buildLabel(""));
        debug.setVerticalAlignment(JLabel.TOP);
        debug.setForeground(Color.DARK_GRAY);
        debug.setBounds(0, 0, screenSize.width, screenSize.height);
        frame.add(debug);

        img = new JLabel();
        img.setBounds(0, 0, screenSize.width, screenSize.height);
        frame.add(img);

        setShortcutListener(frame);
        setAllBlack(frame);
        frame.setSize(screenSize.width, screenSize.height);//400 width and 500 height  
        frame.setLayout(null);//using no layout managers  
        frame.setUndecorated(true);
        frame.setBackground(Color.BLACK);

        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);//making the frame visible  

//        int returnVal = fc.showOpenDialog(frame);
    }

    private void setShortcutListener(JFrame frame) {
        List<Component> comp_list = hl.getAllComponents(frame);
        comp_list.forEach((component) -> {
            component.addKeyListener(hl.getShortcutKeyListener());
        });
    }

    private void setAllBlack(JFrame frame) {
        List<Component> comp_list = hl.getAllComponents(frame);
        comp_list.forEach((component) -> {
            component.setBackground(Color.black);
        });
    }

    int recurse = 0;

    String defaultFolder = "C:\\Users\\stark\\Downloads\\tor\\vtffsd";

    public void setImg() {
        try {
            JFileChooser jfc = new JFileChooser(defaultFolder);
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.showOpenDialog(null);
            File f = jfc.getSelectedFile();
            System.out.println(f.getAbsoluteFile());
            defaultFolder = f.getAbsolutePath();
            saveProps();
            setPics(listFilesForFolder(f, recurse));
            sliderun();
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public List<File> listFilesForFolder(final File folder, int sub) {
        List<File> lf = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                if (sub != 0) {
                    lf = Stream.concat(lf.stream(), listFilesForFolder(fileEntry, sub--).stream()).collect(Collectors.toList());
                }
            } else {
                String mimetype = new MimetypesFileTypeMap().getContentType(fileEntry);
                String type = mimetype.split("/")[0];
                if (type.equals("image")) {
                    System.out.println(fileEntry.getName() + ": added");
                    lf.add(fileEntry);
                } else {
                    System.out.println(fileEntry.getName() + ": not an image");
                }
            }
        }
        return lf;
    }
    List<File> pic;
    int npic = 0;

    synchronized void setPics(List<File> pic) {
        this.pic = pic;
    }

    synchronized File getPic() {
        int p = npic();
        System.out.println(p);
        return pic.get(p);
    }

    synchronized private int npic() {
        System.out.println("size:" + pic.size());
        if (npic < 0) {
            npic = 0;
        }
        if (npic >= pic.size()) {
            npic = 0;
        }
        int n = npic;
        npic++;
        return n;
    }

    synchronized private int getnpic() {
        return npic;
    }

    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    boolean running = false;
    Future<?> future;
    Long seconds = 2L;

    public void setTime(Long l) {
        seconds = l;
    }

    public void sliderun() {
        if (running) {
            System.out.println("pause");
            future.cancel(true);
            running = false;
        } else {
            System.out.println("start");
            future = executorService.scheduleAtFixedRate(() -> {
                switchpic();
            }, 0, seconds, TimeUnit.SECONDS);
            running = true;
        }
    }

    private void switchpic() {
        File file = getPic();
        BufferedImage bi;
        try {
            bi = ImageIO.read(file);
            System.out.println(file.getName() + " " + bi.getWidth() + "x" + bi.getHeight());
            debug.setText(hl.buildLabel(file.getName() + " " + bi.getWidth() + "x" + bi.getHeight()));
            Dimension d = setD(bi.getWidth(), bi.getHeight());
            img.setIcon(
                    new ImageIcon(new javax.swing.ImageIcon(bi).getImage()
                            .getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH)));
            img.setHorizontalAlignment((int) JLabel.CENTER_ALIGNMENT);

        } catch (IOException ex) {
            Logger.getLogger(Slide.class
                    .getName()).log(Level.SEVERE, null, ex);
            debug.setText(hl.buildLabel("ERROR " + file.getName()));
        }
    }

    void exiton() {
        executorService.shutdown();
        frame.dispose();
    }

    private Dimension setD(int width, int height) {
        int swi = screenSize.width;
        int she = screenSize.height;
        Float sratio = new Float(swi) / new Float(she);
        Float ratio = new Float(width) / new Float(height);
        System.out.println("ratio:" + sratio + " " + ratio);
        int wi = width;
        int he = height;
        if (sratio.equals(ratio)) {
            wi = swi;
            he = she;
            System.out.println("equals");
        } else if (sratio.compareTo(ratio) < 0) {
            System.out.println("superior");
        } else if (sratio.compareTo(ratio) > 0) {
            System.out.println("inferior");
            he = she;
            wi = (int) ((new Float(she) / new Float(height)) * new Float(width));
        }
        System.out.println(width + "x" + height);
        System.out.println(wi + "x" + he);
        Dimension d = new Dimension(wi, he);
        return d;
    }

    void popTimer() {
        boolean wasrunning = running;
        if (wasrunning) {
            sliderun();
        }
        String name = JOptionPane.showInputDialog(frame,
                "timer", null);
        Long l;
        try {
            l = Long.parseLong(name);
        } catch (NumberFormatException e) {
            l = 2L;
        }
        setTime(l);
        if (wasrunning) {
            sliderun();
        }
    }

    void skip(int i) {
        boolean wasrunning = running;
        if (wasrunning) {
            sliderun();
        }
        int s = npic + i;
        if (s < 0) {
            npic = pic.size() - 1 + s;
        } else if (s >= pic.size()) {
            npic = s - pic.size();
        } else {
            npic = s;
        }

        if (wasrunning) {
            sliderun();
        }
    }

    void recur() {
        String name = JOptionPane.showInputDialog(frame,
                "recurse level (-1 for infinity)", null);
        Integer l;
        try {
            l = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            l = 0;
        }
        recurse = l;
    }
    static final String PROPFILE = "slide.ini";

    void saveProps() {
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream(PROPFILE);

            // set the properties value
            prop.setProperty("defaultfolder", defaultFolder);

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    void getProps() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream(PROPFILE);

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("defaultfolder"));
            defaultFolder = prop.getProperty("defaultfolder");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setDebug() {
        debug.setText(hl.buildLabel(""));
    }

}
