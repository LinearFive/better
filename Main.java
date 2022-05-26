import view.ChessGameFrame;

import javax.swing.*;

import view.LoadingPage;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        /*
        LoadingPage loadingPage = new LoadingPage();
        Thread.sleep(2500);
        loadingPage.dispose();

         */

        SwingUtilities.invokeLater(() -> {
            ChessGameFrame mainFrame = new ChessGameFrame(1100, 760);
            mainFrame.setVisible(true);
        });
    }
}
