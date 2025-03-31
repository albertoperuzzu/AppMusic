Hi! This is a Spring-boot application for controlling Spotify and receive the lyrics of the song!
I would like to integrate it with a Translation Service too, but for now it's just a tryout and a personal project, so it keeps evolving!
Down here are the instructions to download and run the program!


Prerequisites:

1. Installation of Git
   - Download Git Installer from https://git-scm.com/downloads/win
   - Click the file .exe and follow the instructions
   - When installation is completed, open the Command Prompt and execute "git --version" to check the installation
  
2. Installation of JDK
   - Download JDK (JDK 17 at least) from https://www.oracle.com/java/technologies/downloads/ and install it
   - Go to Control Panel > System > Advanced system settings > Environment Variables
   - Edit the JAVA_HOME variable with the JDK installation path (e.g., C:\Program Files\Java\jdk-24.x.x)
   - Verify the installation in the command prompt with "java -version"
  
3. Installation of Maven
   - Download Maven from https://maven.apache.org/download.cgi selecting the binary .zip version
   - Extract the content into a folder (e.g., C:\Program Files\Apache\Maven)
   - Add C:\Program Files\Apache\Maven\apache-maven-x.x.x\bin to the Path environment variable
   - Verify the installation with command "mvn -version"
  

Clone the Repo:

- Create a foleder for the project (e.g., C:\Personal\Projects)
- In command Prompt, navigate to the folder with "cd path" (in my case, "cd C:\Personal\Projects")
- Clone the repo with "git clone https://github.com/albertoperuzzu/AppMusic.git"
- After downloading, navigate to the project folder with "cd AppMusic"
- Open the application.properties file in the src/main/resources/ directory and ensure that configurations are present, otherwise ask the owner or create you own Spotify App


Add File application.properties
- In the path src/main/resources add a file called application.properties
- In file application.properties insert the configuration keys:
  
server.port=9999
spotify.id={the spotify app ID}
spotify.secret={the spotify app secret}
spotify.uri=http://localhost:9999/callback
genius.token={the genius website token}

Remember to add this file, otherwise the App won't work


Run the App:

- To start the application, run the following command: "mvn spring-boot:run"
- The app will be accessible at http://localhost:9999


You're now ready to use AppMusic! 
