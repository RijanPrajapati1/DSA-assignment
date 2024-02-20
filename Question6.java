import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class ImageDownloader extends JFrame {

    private JTextField urlTextField;
    private JTextArea statusTextArea;
    private JButton downloadButton;
    private JButton clearButton;
    private JButton pauseButton; // Added pause button
    private JButton resumeButton; // Added resume button
    private JButton cancelButton; // Added cancel button
    private ExecutorService executorService;
    private AtomicBoolean isPaused; // Added flag for pausing
    private AtomicBoolean isCancelled; // Added flag for canceling

    public ImageDownloader() {
        initComponents();
        executorService = Executors.newFixedThreadPool(5); // Create a thread accroding to question to handle multiple threads (i have included 5)
        isPaused = new AtomicBoolean(false); // Initialize pause flag
        isCancelled = new AtomicBoolean(false); // Initialize cancel flag
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Multithreaded Image Downloader");
        setSize(840, 450);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel urlLabel = new JLabel("Image URL:");
        urlTextField = new JTextField(30);
        downloadButton = new JButton("Download");
        clearButton = new JButton("Clear");
        pauseButton = new JButton("Pause"); // Initialize pause button
        resumeButton = new JButton("Resume"); // Initialize resume button
        cancelButton = new JButton("Cancel"); // Initialize cancel button

        inputPanel.add(urlLabel);
        inputPanel.add(urlTextField);
        inputPanel.add(downloadButton);
        inputPanel.add(clearButton);
        inputPanel.add(pauseButton); // Add pause button
        inputPanel.add(resumeButton); // Add resume button
        inputPanel.add(cancelButton); // Add cancel button

        statusTextArea = new JTextArea(15, 50);
        statusTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        downloadButton.addActionListener(this::downloadButtonActionPerformed);
        clearButton.addActionListener(this::clearButtonActionPerformed);
        pauseButton.addActionListener(this::pauseButtonActionPerformed); // Add action listener for pause button to work on the basis 
        resumeButton.addActionListener(this::resumeButtonActionPerformed); // Add action listener for resume button
        cancelButton.addActionListener(this::cancelButtonActionPerformed); // Add action listener for cancel button

        getContentPane().add(mainPanel);
    }

    private void downloadButtonActionPerformed(ActionEvent evt) {
        String imageUrl = urlTextField.getText();
        if (!imageUrl.isEmpty()) {
            downloadImage(imageUrl);
            urlTextField.setText(""); // Clear the URL input field after clicking download button
        } else {
            showMessage("Please enter a valid image URL.");
        }
    }
    

    private void clearButtonActionPerformed(ActionEvent evt) {
        urlTextField.setText("");
        statusTextArea.setText("");
    }

    private void pauseButtonActionPerformed(ActionEvent evt) {
        isPaused.set(true); // Set the pause flag 
        showMessage("Downloads paused.");
    }

    private void resumeButtonActionPerformed(ActionEvent evt) {
        isPaused.set(false); // Reset the pause flag (pasue goes to false after clicking on resume)
        showMessage("Downloads resumed.");
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        isCancelled.set(true); // Set the cancel flag to cancel the downloading image
        showMessage("Downloads canceled.");
        // Reset the cancel flag
        isCancelled.set(false);
    }
    

    private void downloadImage(String imageUrl) {
        executorService.execute(() -> {
            try {
                URL url = new URL(imageUrl);  //execute the download task 
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //connecion to url
                int responseCode = connection.getResponseCode(); //respnse from http code

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String contentType = connection.getContentType();
                    if (contentType != null && contentType.startsWith("image")) {  //checking type
                        String fileName = url.getFile();  //get filename
                        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);  //extract filename
                        String savedFileName = getUniqueFileName(fileName); //unique for each
                        Path outputPath = Paths.get(System.getProperty("user.home"), savedFileName); //ouput path

                        try (InputStream inputStream = connection.getInputStream();
                             OutputStream outputStream = new FileOutputStream(outputPath.toFile())) {  //for saving

                            byte[] buffer = new byte[4096];   //creating buffer to read data
                            int bytesRead;
                            long totalBytesRead = 0;
                            long fileSize = connection.getContentLengthLong();

                            while ((bytesRead = inputStream.read(buffer)) != -1) {  //data read until end of stream reached
                                if (isPaused.get()) { // Check if paused
                                    showMessage("Downloads paused.");
                                    while (isPaused.get()) {
                                        Thread.sleep(1000); // Sleep for 1 second before checking again
                                    }
                                    showMessage("Downloads resumed.");
                                }
                                if (isCancelled.get()) { // Check if cancelled
                                    showMessage("Downloads canceled.");
                                    return;
                                }
                                outputStream.write(buffer, 0, bytesRead);
                                totalBytesRead += bytesRead;

                                int progress = (int) ((totalBytesRead * 100) / fileSize);
                                showProgress(savedFileName, progress);
                            }
                            showMessage("Download completed: " + savedFileName);
                        }
                    } else {
                        showMessage("Invalid URL or URL does not point to an image: " + imageUrl);
                    }
                } else {
                    showMessage("Failed to connect to the server. HTTP error code: " + responseCode);
                }
            } catch (Exception e) {
                showMessage("Error downloading image: " + e.getMessage());
            }
        });
    }

    private String getUniqueFileName(String fileName) {
        String baseName = fileName.substring(0, Math.min(fileName.lastIndexOf('.'), 255));  //baselength:225
        String extension = fileName.substring(fileName.lastIndexOf('.'));  //extension
        Path filePath = Paths.get(System.getProperty("user.home"), fileName);        //sending the save file path 
        int count = 1;

        while (Files.exists(filePath)) {  //checking if same file exists else change by adding _1_2 in last of it as extension
            String newFileName = MessageFormat.format("{0}_{1}{2}", baseName, count++, extension);
            filePath = Paths.get(System.getProperty("user.home"), newFileName);
        }

        return filePath.getFileName().toString();
    }

    private void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("Download completed")) {
                statusTextArea.append(message + "\n\n"); // Append with new line after download completed
            } else {
                statusTextArea.append(message + "\n"); // Append with new line
            }
        });
    }
    
    private void showProgress(String fileName, int progress) {
        SwingUtilities.invokeLater(() -> {
            String progressMessage = "Downloading " + fileName + ": " + progress + "%";
            int lengthToRemove = statusTextArea.getText().lastIndexOf('\n') + 1; // Find the start of the last line
            statusTextArea.replaceRange(progressMessage, lengthToRemove, statusTextArea.getText().length()); // Replace the last line with the new progress message
        });
    }
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageDownloader().setVisible(true);
        });
    }
}
