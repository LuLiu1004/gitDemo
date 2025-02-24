import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Frontend implements FrontendInterface {

  private Scanner scanner;
  private BackendInterface backend;

  private Integer minEnergy;
  private Integer maxEnergy;
  private Integer danceabilityThreshold;


  // Constructors
  public Frontend(Scanner scanner, BackendInterface backend) {
    this.scanner = scanner;
    this.backend = backend;

  }

  /**
   * Displays instructions for the syntax of user commands.
   * And then repeatedly gives the user an opportunity to issue new commands until they enter "quit".
   * Use the evaluateSingleCommand method below to parse and run each command entered by the user.
   * If the backend ever throws any exceptions,
   * they should be caught here and reported to the user.  The user should then continue
   * to be able to issue subsequent commands until they enter "quit".
   * This method must use the scanner passed into the constructor to read commands input by the user.
   *
   */
  @Override
  public void runCommandLoop() {

    // Display welcome message
    System.out.println("Welcome to the iSongly program!");
    //Displays instructions for the syntax of user commands.
    displayCommandInstructions();

    while (scanner.hasNextLine()) {

      System.out.print(">>");
      String command = this.scanner.nextLine().trim();
      if(command.isEmpty()) {
        System.out.println("Empty commands.Enter help for command instructions.");
        continue;
      }

      if(command.equals("quit")) {
        System.out.println("Thank you for using iSongly program.");
        break;
      }

      // Use the evaluateSingleComman method below to parse and run each command entered by the
      // user.
      // If the backend ever throws any exceptions, they should be caught here and reported to
      // the user.
      // The user should then continue to be able to issue subsequent commands until they enter "quit".

      try{
        executeSingleCommand(command);}
      catch (Exception e){
        //handle other backend exceptions
        System.out.println("Error:" + e.getMessage());
      }

    }

  }

  /**
   * Displays instructions for the user to understand the syntax of commands that they are able to
   * enter.
   * This should be displayed once from the command loop, before the first user command is
   * read in, and then later in response to the user entering the command: help.
   *
   * The lowercase words in the following examples are keywords that the user must match exactly in
   * their commands, while the upper case words are placeholders for arguments that the user can
   * specify.
   * The following are examples of valid command syntax that your frontend should be able
   * to handle correctly.
   *
   * load FILEPATH
   * energy MAX
   * energy MIN to MAX
   * danceability MIN
   * show MAX_COUNT
   * show most recent
   * help
   * quit
   */
  @Override
  public void displayCommandInstructions() {
    System.out.println("================= Command Syntax Instructions =================");
    System.out.println("The lowercase words are keywords that need matching exactly");
    System.out.println("The UPPERCASE words are placeholders for argument that you can specify");

    System.out.println("================= Available Commands ==========================");
    System.out.println(">>load FILEPATH      :load a single file from a specified path");
    System.out.println(">>energy MAX         :set the maximum energy level of songs to return");
    System.out.println(">>energy MIN to MAX  :set the range of songs to return from MIN to MAX");
    System.out.println(">>danceability MIN   :set the threshold danceability of songs to return");
    System.out.println(">>show MAX_COUNT     :displays up to MAX_COUNT filtered songs");
    System.out.println(">>show most recent   :displays five most recent songs");
    System.out.println(">>help               :displays command instructions");
    System.out.println(">>quit               :ends this program");


  }

  /**
   * This method takes a command entered by the user as input. It parses that command to determine
   * what kind of command it is, and then makes use of the backend (which was passed to the
   * constructor) to update the state of that backend.
   * When a show or help command are issued, this
   * method prints the appropriate results to standard out.
   * When a command does not follow the
   * syntax rules described above, this method should print out an error message that describes at
   * least one defect in the syntax of the provided command argument.
   *
   * Some notes on the expected behavior of the different commands:
   * load: results in backend loading data from specified path
   * energy: updates backend's range of songs to return
   *                            should not result in any songs being displayed
   * danceability: updates backend's filter threshold
   *                            should not result in any songs being displayed
   * show: displays list of songs with currently set thresholds
   *                       MAX_COUNT: argument limits the number of song titles displayed
   *                       to the first MAX_COUNT in the list returned from backend
   *                       most recent: argument displays results returned from the
   *                       backend's fiveMost method
   * help: displays command instructions
   * quit: ends this program (handled by runCommandLoop method above) (do NOT use System.exit(),
   * as this will interfere with tests)
   *
   * @param command
   */
  @Override
  public void executeSingleCommand(String command)  {


    // trim the command to avoid any leading or trailing spaces
    command = command.trim();
    // parse the command
    String[] commandParts = command.split("\\s+");

    switch(commandParts[0]) {
      case "load":{
        try{handleLoadCommand(commandParts);
        } catch (IOException e){
          System.out.println("Error: IO issues during processing the file.");
        }

        break;
      }
      case "energy": {
        handleEnergyCommand(commandParts);
        break;
      }
      case "danceability":{
        handleDanceabilityCommand(commandParts);
        break;
      }
      case "show":{
        handleShowCommand(commandParts);
        break;
      }
      case "help":{
        handleHelpCommand(commandParts);
        break;
      }
      case "quit":{
        handleQuitCommand(commandParts);
        break;
      }
      default:
          System.out.println("Unknown command. Enter help for command instructions.");
    }


  }

  /**
   * Handle the load command
   * load: results in backend loading data from specified path
   * @param tokens the input command line
   * @throws IOException
   */
  private void handleLoadCommand(String[] tokens) throws IOException {
    try{
      if(tokens.length < 2){
        throw new IllegalArgumentException("Invalid syntax -> missing FILEPATH");

      }else if(tokens.length > 2){

        throw new IllegalArgumentException("Invalid syntax -> more than one FILEPATH");
      }
      else{
        String filePath = tokens[1].trim();
        backend.readData(filePath);
        System.out.println("Successfully loaded " + filePath);
      }
    }catch(IllegalArgumentException e){
      System.out.println("Error:" + e.getMessage());
    }


    return;

  }

  /**
   * Handle the energy command
   * energy: updates backend's range of songs to return
   *         should not result in any songs being displayed
   * @param tokens the input command line
   */
  private void handleEnergyCommand(String[] tokens) {
    try{
      if(tokens.length == 2){
        int high=Integer.parseInt(tokens[1].trim());
        maxEnergy = high;
        backend.getRange(this.minEnergy,this.maxEnergy);
        System.out.println("Update the maximum energy level to "+maxEnergy);

      }else if(tokens.length == 4 && tokens[2].equals("to")){

        int low=Integer.parseInt(tokens[1].trim());
        int high=Integer.parseInt(tokens[3].trim());

        minEnergy = low;
        maxEnergy = high;
        backend.getRange(minEnergy,maxEnergy);
        System.out.println("Update the energy level range : " +this.minEnergy+" to "+this.maxEnergy);

      }else{
        throw new IllegalArgumentException ("Invalid syntax -> Incorrect number of arguments." );
      }
    } catch (NumberFormatException e) {
      System.out.println("Error:Invalid syntax -> energy MIN to MAX or energy MAX, MIN or MAX is a " +
          "valid integer.");
    }catch (IllegalArgumentException e){
      System.out.println("Error:"+e.getMessage());
    }

    return;

  }

  /**
   * Handle the danceability command
   *        danceability: updates backend's filter threshold
   *    *                 should not result in any songs being displayed
   * @param tokens the input command line
   */
  private void handleDanceabilityCommand(String[] tokens) {
    try{
      if(tokens.length != 2){
        throw new IllegalArgumentException("Invalid syntax -> danceability MIN, incorrect number " +
            "of arguments.");
      }
      int minDanceability = Integer.parseInt(tokens[1].trim());

      danceabilityThreshold = minDanceability;
      backend.filterSongs(danceabilityThreshold);
      System.out.println("Update the threshold of danceability to " + this.danceabilityThreshold);

    } catch (NumberFormatException e){
      System.out.println("Error:Invalid syntax -> danceability MIN, MIN is a valid integer.");
    } catch(IllegalArgumentException e){
      System.out.println("Error:"+e.getMessage());
    }
    //System.out.println("threshold: "+threshold);
    return;

  }

  /**
   * Handle the show command
   *        show: displays list of songs with currently set thresholds
   *              MAX_COUNT: argument limits the number of song titles displayed
   *                         to the first MAX_COUNT in the list returned from backend
   *             most recent: argument displays results returned from the
   *    *                     backend's fiveMost method
   *
   * @param tokens the input command line
   */
  private void handleShowCommand(String[] tokens) {
    // retrieve the songs based on the user's input of min and max energy level and the threshold
    // of danceability
    //List<String> returnedSongList = backend.getRange(minEnergy, maxEnergy);
    //returnedSongList= backend.filterSongs(danceabilityThreshold);
    
    if(tokens.length == 3 && tokens[1].equals("most")&& tokens[2].equals("recent")){
      List<String> mostRecent = backend.fiveMost();
      displaySongs(mostRecent,mostRecent.size());
    }
    else if (tokens.length == 2 ) {
      try{
        int max_count = Integer.parseInt(tokens[1].trim());
        if(max_count<0){
          throw new IllegalArgumentException("Invalid syntax -> show MAX_COUNT, MAX_COUNT is a " +
              "non-negative integer. ");
        }
        List<String> retrievedSongs = backend.filterSongs(danceabilityThreshold);


        displaySongs(retrievedSongs,max_count);

      }catch (NumberFormatException e){
        System.out.println("Error:Invalid syntax -> show MAX_COUNT, MAX_COUNT is a " +
            "valid integer.");
      }catch (IllegalArgumentException e){
        System.out.println("Error:"+e.getMessage());
      }
    }
    else{
      throw new IllegalArgumentException("Invalid syntax -> show most recent or show " +
          "MAX_COUNT, incorrect number of arguments.");
    }

  }

  /**
   * Handle the help command
   *        help: displays command instructions
   *
   * @param tokens the command line
   */
  private void handleHelpCommand(String[] tokens) {
    try{
      if(tokens.length == 1) {
        displayCommandInstructions();
        return;
      }else{
        throw new IllegalArgumentException("Invalid syntax -> help command requires exactly one argument.");
      }
    }catch(IllegalArgumentException e){
      System.out.println("Error:"+e.getMessage());
    }


  }

  /**
   * Handle the quit to check if the quit command only has one argument
   * @param tokens the commmand lines
   */
  private void handleQuitCommand(String[] tokens) {
    try{
      if(tokens.length != 1) {
        throw new IllegalArgumentException("Invalid syntax -> quit command requires exactly one " +
            "argument.");
      }
    }catch (IllegalArgumentException e){
      System.out.println("Error:"+e.getMessage());
    }


  }

  /**
   * Display the titles of songs with limited to the minimum number of maxCount or the size of the
   * song lists
   *
   * @param songs the returned song lists
   * @param maxCount the limit to display returned songs
   */
  private void displaySongs(List<String> songs,int maxCount  ) {
    if(songs.isEmpty() || maxCount ==0){
      System.out.println("No songs found.");
      return;
    }


    int size = Math.min(songs.size(), maxCount);
    System.out.print("Found songs: ");
    // Case1 : only one song
    if(size ==1){
      System.out.println(songs.get(0));
      return;
    }
    else{
      for(int i = 0; i < size-1; i++){
        System.out.print(songs.get(i)+", ");
      }
      System.out.println(songs.get(size-1));
    }


  }

}
