/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forow.stark.slide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.swing.JLayeredPane;
import javax.swing.border.EmptyBorder;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_marquee_position_e;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 *
 * @author stark
 */
public class Slide {

    private final EmbeddedMediaPlayerComponent vlc;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new NativeDiscovery().discover();
        Slide slide = new Slide();

    }

    static final String PROPFILE = "slide.ini";
    JButton btnExit;
    JLayeredPane panel;
    JFrame frame;
    final JFileChooser fc = new JFileChooser();
    HeyListen hl;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    JLabel img;
    JLabel debug;
    int recurse = 0;
    String defaultFolder = "C:\\Users\\stark\\Downloads\\tor\\vtffsd";
    List<File> pic = new ArrayList<>();
    int npic = 0;
    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    boolean running = false;
    Future<?> future;
    Long seconds = 2L;
    File currentPic;
    List<PlAdd> propPls = new ArrayList<>();
    List<File> playlistFiles;

    Slide() {
        getProps();
        frame = new JFrame();
        frame.setName("slider");
        panel = new JLayeredPane();
        panel.setPreferredSize(screenSize);
        hl = new HeyListen(this, panel);
        panel.setName("main frame");

//        JButton b = new JButton("click");//creating instance of JButton 
//        b.setName("why");
//        b.setBounds(0, 0, 0, 0);//x axis, y axis, width, height  
//        b.addActionListener((java.awt.event.ActionEvent e) -> {
//            setImg();
//        });
//        panel.add(b);//adding button in JFrame  
        img = new JLabel();
        img.setBounds(0, 0, screenSize.width, screenSize.height);

        panel.add(img, new Integer(50));

        vlc = new EmbeddedMediaPlayerComponent();
        vlc.getMediaPlayer().addMediaPlayerEventListener(getVlcEvent());
                vlc.setBounds(0, 0, screenSize.width, screenSize.height);
        vlc.setVisible(false);
        panel.add(vlc, new Integer(60));

        debug = new JLabel();
        debug.setText(hl.buildLabel(""));
        debug.setVerticalAlignment(JLabel.TOP);
        debug.setForeground(Color.DARK_GRAY);
        debug.setBounds(0, 0, screenSize.width, screenSize.height);
        panel.add(debug, new Integer(100));

        panel.setOpaque(true);
        panel.setVisible(true);

//        setShortcutListener(frame);
        setAllBlack(frame);
        frame.setSize(screenSize.width, screenSize.height);//400 width and 500 height  
        frame.setBackground(Color.BLACK);

//        frame.setLayout(null);//using no layout managers  
        frame.setUndecorated(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
//        frame.add(panel);
        frame.setContentPane(panel);
        frame.setVisible(true);
//        frame.pack();
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
//                        setImg();

//        int returnVal = fc.showOpenDialog(frame);
    }

    MediaPlayerEventAdapter getVlcEvent() {
        return new MediaPlayerEventAdapter() {
            @Override
            public void opening(MediaPlayer mediaPlayer) {
                System.out.println("opening mediaplayed");
                super.opening(mediaPlayer);
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                System.out.println("stopped mediaplayed");
                super.stopped(mediaPlayer);
//                        vlc.release();
//                switchpic();
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.out.println("error mediaplayed");
                super.error(mediaPlayer);
//                        vlc.release();
//                switchpic();
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                super.finished(mediaPlayer);
//                        vlc.release();
                switchpic();
            }

            @Override
            public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
                System.out.println("media changed");
                super.mediaChanged(mediaPlayer, media, mrl);
            }

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                System.out.println("media playing");
                super.playing(mediaPlayer); 
            }
            
            

        };
    }

    private void resetNpic() {
        npic = -1;
    }

    private class MyDispatcher implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            return hl.dispatch(e);
        }
    }

//    private void setShortcutListener(JFrame frame) {
//        List<Component> comp_list = hl.getAllComponents(frame);
//        comp_list.forEach(System.out::println);
//        comp_list.forEach((component) -> {
//            component.addKeyListener(hl.getShortcutKeyListener());
//        });
//    }
    private void setAllBlack(JFrame frame) {
        List<Component> comp_list = hl.getAllComponents(frame);
        comp_list.forEach((component) -> {
            component.setBackground(Color.black);
        });
    }

    void setPImg() {
        hl.enableKeyEvents(false);
        resetNpic();
        try {
            JFileChooser jfc = new JFileChooser("playlist");
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.showOpenDialog(null);
            File f = jfc.getSelectedFile();
            System.out.println("pimgabsfile: " + f.getAbsoluteFile());

            setPics(listFilesForProperties(f));
            sliderun();
        } catch (Exception e) {
            System.out.println("ex: " + e);
            e.printStackTrace();
        }
        hl.enableKeyEvents(true);
    }

    public void setImg() {
        hl.enableKeyEvents(false);
        resetNpic();
        try {
            JFileChooser jfc = new JFileChooser(defaultFolder);
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.showOpenDialog(null);
            File f = jfc.getSelectedFile();
            System.out.println("setimg file: " + f.getAbsoluteFile());
            defaultFolder = f.getAbsolutePath();
            saveProps();
            setPics(listFilesForFolder(f, recurse));
            sliderun();
        } catch (Exception e) {
            System.out.println("ex: " + e);
            e.printStackTrace();
        }
        hl.enableKeyEvents(true);
    }

    private List<File> listFilesForProperties(File f) {
        List<File> lf = new ArrayList<>();
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream(f);
            prop.load(input);
            prop.keySet().forEach(k -> lf.add(new File((String) k)));
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
        return lf;
    }

    public List<File> listFilesForFolder(final File folder, int sub) {
        List<File> lf = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                if (sub != 0) {
                    lf = Stream.concat(lf.stream(), listFilesForFolder(fileEntry, sub--).stream()).collect(Collectors.toList());
                }
            } else {
                if (isPicOrVid(fileEntry)) {
                    lf.add(fileEntry);
                } else {
                    System.out.println(fileEntry.getName() + ": not an image");
                }
            }
        }
        System.out.println("fileList: " + lf.size());
        lf.forEach(f -> System.out.println(f.getName() + " p:" + isPic(f) + " v:" + isVid(f)));
        return lf;
    }

    private boolean isPicOrVid(File fileEntry) {
        if (isPic(fileEntry)) {
            return true;
        }
        return isVid(fileEntry);
    }

    private boolean isPic(File file) {
        String mimetype = new MimetypesFileTypeMap().getContentType(file);
        String type = mimetype.split("/")[0];
        if (type.equals("image")) {
            return true;
        }
        if (file.getName().endsWith(".jpg")) {
            return true;
        }
        if (file.getName().endsWith(".jpeg")) {
            return true;
        }
        return file.getName().endsWith(".png");
    }

    private boolean isVid(File file) {
        if (file.getName().endsWith(".webm")) {
            return true;
        }
        return file.getName().endsWith(".mp4");
    }

    synchronized void setPics(List<File> pic) {
        this.pic = pic;
    }

    synchronized File getPic() {
        int p = npic();
        System.out.println("getPic p: " + p);
        currentPic = pic.get(p);
        popPropUpdate();
        return currentPic;
    }

    void popPropUpdate() {
        if (popPlsOpen) {
            for (PlAdd propPl : propPls) {
                propPl.update();
            }
        }
    }

    synchronized private int npic() {
        npic++;
        while (true) {
            if (npic < 0) {
                npic = pic.size() - 1 + npic;
            } else if (npic >= pic.size()) {
                npic = npic - pic.size();
            } else {
                break;
            }
        }
        return npic;
    }

    void skip(int i) {
        boolean wasrunning = running;
        if (wasrunning) {
            sliderun();
        }
        System.out.println("======s1 " + npic + " + " + i);
        int s = npic + i;
        while (true) {
            if (s < -1) {
                npic = pic.size() + s;
            } else if (s >= pic.size()) {
                npic = s - pic.size();
            } else {
                npic = s;
                System.out.println("======s2 " + npic + " (" + i + ")");
                if (wasrunning) {
                    sliderun();
                }
                break;
            }
            s = npic;
        }
    }

    synchronized private int getnpic() {
        return npic;
    }

    public void setTime(Long l) {
        seconds = l;
    }

    public void sliderun() {
        if (vlc.isVisible()) {
            if (running) {
                vlc.getMediaPlayer().pause();
            } else {
                vlc.getMediaPlayer().start();
            }
        } else {
            if (running) {
                System.out.println("pause");
                future.cancel(true);
                running = false;
            } else {
                System.out.println("start");
                running = true;
                timeIt();
            }
        }
    }

    private void timeIt() {
        System.out.println("timeit");
        if (running) {
            future = executorService.schedule(() -> {
                switchpic();
            }, seconds, TimeUnit.SECONDS);
        }
    }

    private void switchpic() {
        File file = getPic();
        if (isPic(file)) {
            System.out.println("next is pic: " + file.getName());
            if (vlc.isVisible()) {
                vlc.setVisible(false);
                panel.revalidate();
            }

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
            timeIt();
        } else if (isVid(file)) {
            switchpic();
//            System.out.println("next is vid: " + file.getName());
//            if (!vlc.isVisible()) {
//                vlc.setVisible(true);
//            }
//            try {
//                vlc.getMediaPlayer().setEnableKeyInputHandling(false);
//                vlc.getMediaPlayer().setEnableMouseInputHandling(false);
//                vlc.getMediaPlayer().setMarqueeText(file.getName());
//                vlc.getMediaPlayer().setMarqueeSize(20);
//                vlc.getMediaPlayer().setMarqueeColour(Color.GRAY);
//                vlc.getMediaPlayer().setMarqueeTimeout(1000);
//                vlc.getMediaPlayer().setMarqueePosition(libvlc_marquee_position_e.bottom_right);
//                vlc.getMediaPlayer().setMarqueeOpacity(0.8f);
//                vlc.getMediaPlayer().enableMarquee(true);
//                vlc.getMediaPlayer().playMedia(file.getCanonicalPath());
//            } catch (IOException ex) {
//                Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
//                debug.setText(hl.buildLabel("ERROR " + file.getName()));
//                System.out.println("error: " + file.getName());
//                switchpic();
//            }
        }
    }

    void exiton() {
        vlc.release();
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
        String name = JOptionPane.showInputDialog(panel,
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

    void recur() {
        hl.enableKeyEvents(false);
        String name = JOptionPane.showInputDialog(panel,
                "recurse level (-1 for infinity)", recurse);
        Integer l;
        try {
            l = Integer.parseInt(name);
        } catch (NumberFormatException e) {
            l = 0;
        }
        recurse = l;
        hl.enableKeyEvents(true);
    }

    void saveProps() {
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream(PROPFILE);

            // set the properties value
            prop.setProperty("defaultfolder", defaultFolder);
            prop.setProperty("timer", seconds.toString());

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
            System.out.println("default folder: " + prop.getProperty("defaultfolder"));
            defaultFolder = prop.getProperty("defaultfolder");
            System.out.println("timer: " + prop.getProperty("timer"));
            seconds = prop.getProperty("timer") == null
                    ? 3L
                    : Long.parseLong(prop.getProperty("timer"));
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
        System.out.println("vlc is "+(vlc.isVisible()?"visible":"not visible"));
        debug.setText(hl.buildLabel(""));
    }

    void getPlaylists() {
        playlistFiles = new ArrayList<>();
        try {
            Files.createDirectory(Paths.get("playlist"));
        } catch (IOException ex) {
        }
        File playFolder = new File("playlist");
        for (final File fileEntry : playFolder.listFiles()) {
            if (fileEntry.isDirectory()) {
            } else {
                System.out.println("filepath: " + fileEntry.getAbsolutePath());
                if (playlistFiles == null) {
                    playlistFiles = new ArrayList<>();
                }
                playlistFiles.add(fileEntry);
            }
        }
    }

    List<File> plContains(String pic) {
        ArrayList<File> af = new ArrayList<>();
        for (File playlistFile : playlistFiles) {
            if (plContain(playlistFile, pic)) {
                af.add(playlistFile);
            }
        }
        return af;
    }

    boolean plContain(File pl, String pic) {
        boolean b = false;
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(pl);
            prop.load(input);
            if (prop.getProperty(pic) != null) {
                b = true;
            }
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
        return b;
    }

    void plAddPic(File propFile, File pic) {
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(propFile);
            prop.load(input);
            output = new FileOutputStream(propFile);
            prop.setProperty(pic.getAbsolutePath(), "OK");
            prop.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
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

    void plDelPic(File propFile, File pic) {
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(propFile);
            prop.load(input);
            output = new FileOutputStream(propFile);
            prop.remove(pic.getAbsolutePath());
            prop.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
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

    private void updatePl(PlAdd plo) {
        if (currentPic == null) {
            System.out.println("null pic");
            return;
        }
        if (plo.contain) {
            plDelPic(plo.pl, currentPic);
        } else {
            plAddPic(plo.pl, currentPic);
        }
        popPropUpdate();
    }

    void sortPic() {
//        boolean wasrunning = running;
//        if (wasrunning) {
//            sliderun();
//        }
        System.out.println("sort pic");
        if (currentPic != null) {
            System.out.println("curpicpath: " + currentPic.getAbsolutePath());
        }
        popPlsAdd();
    }
    public boolean popPlsOpen = false;

    private void popPlsAdd() {
        System.out.println("pop pl:" + popPlsOpen);
        propPls.forEach(System.out::println);
        System.out.println("end prop pl");
        if (popPlsOpen) {
            propPls.forEach(panel::remove);
            propPls = new ArrayList<>();
            popPlsOpen = false;
            panel.revalidate();
            panel.repaint();
            return;
        }
        popPlsOpen = true;
        getPlaylists();
        int lheght = 50;
        int lwidth = 500;
        int step = 1;
        int i = 1;
        System.out.println("playlist files count: " + playlistFiles.size());
        for (File playlistFile : playlistFiles) {
            System.out.println("playlist name: " + playlistFile.getAbsolutePath());
            boolean contains = false;
            if (currentPic != null) {
                contains = plContain(playlistFile, currentPic.getAbsolutePath());
            }

            PlAdd plAdd = new PlAdd(playlistFile, contains, this);
            plAdd.set();
            plAdd.setBounds(5, screenSize.height - (i * lheght), lwidth, lheght - step);
            propPls.add(plAdd);
            panel.add(plAdd, new Integer(150));

            panel.revalidate();
            panel.repaint();
//            frame.validate();
//            frame.repaint();
//            frame.pack();
            System.out.println("pladd:" + plAdd.getParent());
            i++;
        }
    }
    static final Color plExited = Color.DARK_GRAY;
    static final Color plHover = new Color(200, 200, 200);
    static final Color plClicked = Color.MAGENTA;
    static final Color plContains = Color.GREEN;
    static final Color plRemove = Color.RED;
    static final Color plTxt = Color.WHITE;

    class PlAdd extends JLabel {

        public File pl;
        public boolean contain;
        public PlAdd plo;
        public Slide sl;

        public PlAdd(File pl, boolean contains, Slide sl) {
            super();
            this.pl = pl;
            this.contain = contains;
            this.sl = sl;
            this.plo = this;
        }

        public void update() {
            if (currentPic != null) {
                contain = plContain(pl, currentPic.getAbsolutePath());
            } else {
                contain = false;
            }
            this.setForeground(contain ? plContains : plTxt);
        }

        public void set() {
            this.setText(pl.getAbsolutePath());
            this.setName(pl.getName());
            this.setBackground(plExited);
            this.setForeground(contain ? plContains : plTxt);
//            this.setVerticalAlignment(JLabel.TOP);
            this.setBorder(new EmptyBorder(0, 10, 0, 0));
            this.setOpaque(true);
            this.setVisible(true);
            this.addMouseListener(new MouseListener() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    plo.setBackground(new Color(200, 200, 200));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    plo.setBackground(plExited);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    sl.updatePl(plo);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    plo.setBackground(plClicked);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    plo.setBackground(plExited);
                }
            });
        }

    }

}
