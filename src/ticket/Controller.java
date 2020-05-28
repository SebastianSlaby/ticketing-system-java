package ticket;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

// FIXME 28.05.2020:
//  - login window
//  - registration
//  -add new Ticket
//  -database connection
//  - ticket status ComboBox
//  - fix the horizontal scrollbars on TableViews
//  - the Notes have to be assigned to a Ticket


public class Controller {
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private TableView ticketTableView;
    @FXML
    private TableColumn<Ticket, String> columnTicketName;
    @FXML
    private TableColumn<Ticket, LocalDate> columnTicketCreationDate;
    @FXML
    private TableView noteTableView;
    @FXML
    private TableColumn<Note, String> columnNoteName;
    @FXML
    private TableColumn<Note, LocalDate> columnNoteCreationDate;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private TextArea ticketTextArea;
    @FXML
    private Button buttonNewNote;
    @FXML
    private ComboBox comboTicketState;
    @FXML
    private ContextMenu tableContextMenu;

    private dbHandler handler;
    private Ticket selectedTicket;


    public void initialize () {
        ticketTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ticket>() {
            @Override
            public void changed(ObservableValue<? extends Ticket> observableValue, Ticket oldValue, Ticket newValue) {
                if(newValue != null) {
                    Ticket ticket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
                    ticketTextArea.setText(ticket.getTicketDetails());
                    noteTableView.setItems(ticket.getNotes().getNotes());
                    noteTableView.getSelectionModel().selectFirst();
//                    comboTicketState.setItems(selectedTicket.getTicketStatus());
                }
            }
        });




        ticketTableView.setItems(Tickets.getInstance().getTickets());
        columnTicketName.setCellValueFactory(new PropertyValueFactory<Ticket, String>("ticketTitle"));
        columnTicketCreationDate.setCellValueFactory(new PropertyValueFactory<Ticket, LocalDate>("ticketDateOfCreation"));
        ticketTableView.getSortOrder().add(columnTicketCreationDate);
        ticketTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ticketTableView.getSelectionModel().selectFirst();


        columnNoteName.setCellValueFactory(new PropertyValueFactory<Note, String>("noteTitle"));
        columnNoteCreationDate.setCellValueFactory(new PropertyValueFactory<Note, LocalDate>("noteDateOfCreation"));
        noteTableView.getSortOrder().add(columnNoteCreationDate);
        noteTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        noteTableView.getSelectionModel().selectFirst();


        noteTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Note>() {
            @Override
            public void changed(ObservableValue<? extends Note> observableValue, Note oldValue, Note newValue) {
                if(observableValue == null) {

                }
                if(newValue != null) {
                    Note note = (Note) noteTableView.getSelectionModel().getSelectedItem();
                    noteTextArea.setText(note.getNoteDetails());
                }
            }
        });

        tableContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Object obj;
                if(noteTableView.isFocused()) obj = noteTableView.getSelectionModel().getSelectedItem();
                else if (ticketTableView.isFocused()) obj = ticketTableView.getSelectionModel().getSelectedItem();
                else obj = null;
                try {
                    deleteItem(obj);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        tableContextMenu.getItems().add(deleteMenuItem);

        ticketTableView.setRowFactory(new Callback<TableView<Ticket>, TableRow<Ticket>>() {
            @Override
            public TableRow<Ticket> call(TableView tableView) {
                TableRow<Ticket> tableRow = new TableRow<>();

                tableRow.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                tableRow.setContextMenu(null);
                            } else {
                                tableRow.setContextMenu(tableContextMenu);
                            }
                        });

                return tableRow;
            }
        });
        columnTicketName.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Ticket, String> call(TableColumn<Ticket, String> ticketStringTableColumn) {
                TableCell<Ticket, String> cell = new TableCell<>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);
                        }
                    }
                };
                return cell;
            }
        });



        noteTableView.setRowFactory(new Callback<TableView<Ticket>, TableRow<Ticket>>() {
            @Override
            public TableRow<Ticket> call(TableView tableView) {
                TableRow<Ticket> tableRow = new TableRow<>();

                tableRow.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                tableRow.setContextMenu(null);
                            } else {
                                tableRow.setContextMenu(tableContextMenu);
                            }
                        });

                return tableRow;
            }
        });

        columnNoteName.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Note, String> call(TableColumn<Note, String> noteStringTableColumn) {
                TableCell<Note, String> cell = new TableCell<>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item);
                        }
                    }
                };
                return cell;
            }
        });
    }

    @FXML
    public void showNewNoteDialog () throws SQLException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Create a New Note");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("noteDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            NoteDialogController controller = fxmlLoader.getController();
            selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
            Note newNote = controller.processResults(selectedTicket);
//            if(noteTableView.getItems().isEmpty()) {
//                System.out.println(noteTableView.getItems().isEmpty());
                noteTableView.setItems(selectedTicket.getNotes().getNotes());
//            }
            noteTableView.getSelectionModel().select(newNote);
        }
    }

    @FXML
    public void showNewTicketDialog () throws SQLException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add a New Ticket");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("ticketDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            TicketDialogController controller = fxmlLoader.getController();
            Ticket newTicket = controller.processResults();
            ticketTableView.getSelectionModel().select(newTicket);
        }
    }

    public void deleteItem (Object obj) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if(obj.getClass().equals(Note.class)) {
            alert.setTitle("Delete Resolver Note");
            alert.setHeaderText("Delete note: " + ((Note) obj).getNoteTitle());
        } else if (obj.getClass().equals(Ticket.class)) {
            alert.setTitle("Delete Ticket");
            alert.setHeaderText("Delete ticket: " + ((Ticket) obj).getTicketTitle());
        }
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && (result.get() == ButtonType.OK)) {
            if(obj.getClass().equals(Note.class)) {
                selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
                selectedTicket.getNotes().deleteNote((Note) obj);
                if(selectedTicket.getNotes().getNotes().isEmpty()){
                noteTextArea.clear();
                }
            }
            else if(obj.getClass().equals(Ticket.class))  {
                Tickets.getInstance().deleteTicket((Ticket) obj);
                if(Tickets.getInstance().getTickets().isEmpty()) {
                ticketTextArea.clear();
                }
            }
        }
    }

    @FXML
    public void deleteSelectedItem () throws SQLException {
        Object obj;
        if(noteTableView.isFocused()) obj = noteTableView.getSelectionModel().getSelectedItem();
        else if (ticketTableView.isFocused()) obj = ticketTableView.getSelectionModel().getSelectedItem();
        else obj = null;
        if(obj != null) {
            deleteItem(obj);
        }
    }

    @FXML
    public void deleteOnKeyPressed (KeyEvent keyEvent) throws SQLException {
        Object obj;
        if(noteTableView.isFocused()) obj = noteTableView.getSelectionModel().getSelectedItem();
        else if (ticketTableView.isFocused()) obj = ticketTableView.getSelectionModel().getSelectedItem();
        else obj = null;
        if(obj != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(obj);
            }
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }
}
