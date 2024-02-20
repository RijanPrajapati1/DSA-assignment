import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

class User {
    private String username;
    private Set<User> followers;
    private Set<User> following;
    java.util.List<String> recommendedContent = new java.util.ArrayList<>();
    

    public User(String username) {
        this.username = username;
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
    }

    public String getUsername() {
        return username;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void addFollower(User follower) {
        followers.add(follower);
    }

    public void removeFollower(User follower) {
        followers.remove(follower);
    }

    public void addFollowing(User followed) {
        following.add(followed);
    }

    public void removeFollowing(User followed) {
        following.remove(followed);
    }
}

class SocialNetworkGraph {
    private Map<String, User> users;
    private Connection connection;

    public SocialNetworkGraph(Connection connection) {
        this.users = new HashMap<>();
        this.connection = connection;
    }

    public void addUser(String username) {
        if (!users.containsKey(username)) {
            users.put(username, new User(username));
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void addFollower(String follower, String followed) {
        addUser(follower);
        addUser(followed);
        User followerUser = getUser(follower);
        User followedUser = getUser(followed);
        if (followerUser != null && followedUser != null) {
            followerUser.addFollowing(followedUser);
            followedUser.addFollower(followerUser);
            try {
                String query = "INSERT INTO follows (follower, followed) VALUES (?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, follower);
                statement.setString(2, followed);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Follower-followed relationship added to database.");
                } else {
                    System.out.println("Failed to add follower-followed relationship to database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFollower(String follower, String followed) {
        User followerUser = getUser(follower);
        User followedUser = getUser(followed);
        if (followerUser != null && followedUser != null) {
            followerUser.removeFollowing(followedUser);
            followedUser.removeFollower(followerUser);
            try {
                String query = "DELETE FROM follows WHERE follower = ? AND followed = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, follower);
                statement.setString(2, followed);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Follower-followed relationship removed from database.");
                } else {
                    System.out.println("Failed to remove follower-followed relationship from database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

class LoginGUI {
    private JFrame frame;
    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private Connection connection;
    private SocialNetworkGraph socialGraph;

    public LoginGUI() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signupButton);

        frame.add(panel);
        frame.setVisible(true);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/social_media_db", "root", "luckyiscat2");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        socialGraph = new SocialNetworkGraph(connection);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticate(username, password)) {
                    frame.dispose();
                    new SocialMediaAppGUI(username, socialGraph, connection);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password");
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignupGUI(connection, socialGraph);
            }
        });
    }

    private boolean authenticate(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String user = resultSet.getString("username");
                socialGraph.addUser(user);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginGUI();
            }
        });
    }
}

class SocialMediaAppGUI {
    private JFrame frame;
    private JPanel panel;
    private JTextArea contentTextArea;
    private JTextField commentField;
    private JButton postButton;
    private JButton followButton;
    private JButton usersButton;
    private JButton myProfileButton; // Add "My Profile" button
    private SocialNetworkGraph socialGraph;
    private String username;
    private JButton logoutButton;
    private Connection connection;

    public SocialMediaAppGUI(String username, SocialNetworkGraph socialGraph, Connection connection) {
        this.username = username;
        this.socialGraph = socialGraph;
        this.connection = connection;

        frame = new JFrame("Social Media App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        contentTextArea = new JTextArea();
        contentTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(contentTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel interactionPanel = new JPanel(new FlowLayout());
        postButton = new JButton("Post");
        followButton = new JButton("Follow");
        usersButton = new JButton("Users");
        myProfileButton = new JButton("My Profile"); // Create "My Profile" button
        interactionPanel.add(postButton);
        interactionPanel.add(followButton);
        interactionPanel.add(usersButton);
        interactionPanel.add(myProfileButton); // Add "My Profile" button
        panel.add(interactionPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        logoutButton = new JButton("Logout");
        interactionPanel.add(logoutButton); // Add the logout button to the interaction panel

        usersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayUserList(); // Call method to display user list
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    frame.dispose();
                    new LoginGUI(); // Redirect to the login screen
                }
            }
        });

        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder functionality for posting content
                String postContent = JOptionPane.showInputDialog(frame, "Enter your post:");
                if (postContent != null && !postContent.isEmpty()) {
                    if (insertPost(username, postContent)) {
                        updateContent(username + ": " + postContent);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to post. Please try again.");
                    }
                }
            }
        });

        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Functionality for following another user
                String followedUser = JOptionPane.showInputDialog(frame, "Enter the username of the user you want to follow:");
                if (followedUser != null && !followedUser.isEmpty()) {
                    socialGraph.addFollower(username, followedUser);
                    JOptionPane.showMessageDialog(frame, "You are now following " + followedUser);
                }
            }
        });

        myProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Display user profile
                displayUserProfile(username);
            }
        });

        retrieveAndDisplayPosts(username);
    }

    private void displayUserList() {
        // Query database to get list of users
        try {
            String query = "SELECT username FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Create a StringBuilder to build the user list
            StringBuilder userList = new StringBuilder();
            userList.append("Users:\n");

            // Append each username to the list
            while (resultSet.next()) {
                String user = resultSet.getString("username");
                userList.append(user).append("\n");
            }

            // Display the user list in a dialog
            JOptionPane.showMessageDialog(frame, userList.toString(), "Users", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void retrieveAndDisplayPosts(String username) {
        try {
            // Retrieve posts from the current user
            String query = "SELECT content FROM posts WHERE user_id = (SELECT id FROM users WHERE username = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String postContent = resultSet.getString("content");
                updateContent(username + ": " + postContent);
            }

            // Retrieve posts from users whom the current user is following
            query = "SELECT posts.content, users.username FROM posts INNER JOIN users ON posts.user_id = users.id WHERE users.username IN (SELECT followed FROM follows WHERE follower = ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String postContent = resultSet.getString("content");
                String postOwner = resultSet.getString("username");
                updateContent(postOwner + ": " + postContent);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }  

    private void updateContent(String newContent) {
        contentTextArea.append(newContent + "\n");
    }

    private boolean insertPost(String username, String postContent) {
        try {
            String query = "INSERT INTO posts (user_id, content) VALUES ((SELECT id FROM users WHERE username = ?), ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, postContent);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    

    private void displayUserProfile(String username) {
        // Retrieve user's followers and following from the database
        try {
            String query = "SELECT followed FROM follows WHERE follower = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            // Create a StringBuilder to build the user profile
            StringBuilder profile = new StringBuilder();
            profile.append("Username: ").append(username).append("\n\n");
            profile.append("Followers:\n");

            // Append each follower to the profile
            while (resultSet.next()) {
                String follower = resultSet.getString("followed");
                profile.append(follower).append("\n");
            }

            // Retrieve user's following
            query = "SELECT follower FROM follows WHERE followed = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            profile.append("\nFollowing:\n");
            // Append each followed user to the profile
            while (resultSet.next()) {
                String followed = resultSet.getString("follower");
                profile.append(followed).append("\n");
            }

            // Display the user profile in a dialog
            JOptionPane.showMessageDialog(frame, profile.toString(), "My Profile", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
