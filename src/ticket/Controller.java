package ticket;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalDate;
import java.util.Optional;

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

    public void initialize () {
        Note note = new Note("xd");
        Note note1 = new Note("xdd");
        ObservableList xd = FXCollections.observableArrayList();
        xd.add(note);
        xd.add(note1);
        Notes.getInstance().setNotes(xd);
        noteTableView.setItems(Notes.getInstance().getNotes());
        columnNoteName.setCellValueFactory(new PropertyValueFactory<Note, String>("note"));
        columnNoteCreationDate.setCellValueFactory(new PropertyValueFactory<Note, LocalDate>("dateOfCreation"));
        noteTableView.getSortOrder().add(columnNoteCreationDate);


        Ticket t1 = new Ticket("xd", "kekw", 1, 1, "2019-01-22");
        Ticket t2 = new Ticket("xdddx", "kekwasda", 1, 1, "2019-03-22");
        Ticket t3 = new Ticket("xdddx", "kekwasda", 1, 1, "2019-03-22");
        ObservableList lolw = FXCollections.observableArrayList();
        lolw.add(t1);
        lolw.add(t2);
        lolw.add(t3);
        Tickets.getInstance().setTickets(lolw);
        ticketTableView.setItems(Tickets.getInstance().getTickets());
        columnTicketName.setCellValueFactory(new PropertyValueFactory<Ticket, String>("note"));
        columnTicketCreationDate.setCellValueFactory(new PropertyValueFactory<Ticket, LocalDate>("dateOfCreation"));
        ticketTableView.getSortOrder().add(columnTicketCreationDate);

        noteTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Note>() {
            @Override
            public void changed(ObservableValue<? extends Note> observableValue, Note oldValue, Note newValue) {
                if(newValue != null) {
                    Note note = (Note) noteTableView.getSelectionModel().getSelectedItem();
                    noteTextArea.setText(note.getNote());
                }
            }
        });
        ticketTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ticket>() {
            @Override
            public void changed(ObservableValue<? extends Ticket> observableValue, Ticket oldValue, Ticket newValue) {
                if(newValue != null) {
                    Ticket ticket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
                    ticketTextArea.setText(ticket.getDescription());
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
                deleteItem(obj);
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
        ticketTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ticketTableView.getSelectionModel().selectFirst();


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

        noteTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        noteTableView.getSelectionModel().selectFirst();
    }

    @FXML
    public void showNewNoteDialog () {
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
            DialogController controller = fxmlLoader.getController();
            Note newNote = controller.processResults();
            noteTableView.getSelectionModel().select(newNote);
        }
    }

    public void deleteItem (Object obj) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if(obj instanceof Note) {
            alert.setTitle("Delete Resolver Note");
            alert.setHeaderText("Delete note: " + ((Note) obj).getNote());
        } else if (obj instanceof Ticket) {
            alert.setTitle("Delete Ticket");
            alert.setHeaderText("Delete ticket: " + ((Note) obj).getNote());
        }
        alert.setContentText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && (result.get() == ButtonType.OK)) {
            if(obj.getClass().equals(Note.class)) Notes.getInstance().deleteResolverNote((Note) obj);
            else if(obj.getClass().equals(Ticket.class)) Tickets.getInstance().deleteTicket((Ticket) obj);
        }
    }

    @FXML
    public void deleteSelectedItem () {
        Object obj;
        if(noteTableView.isFocused()) obj = noteTableView.getSelectionModel().getSelectedItem();
        else if (ticketTableView.isFocused()) obj = ticketTableView.getSelectionModel().getSelectedItem();
        else obj = null;
        if(obj != null) {
            deleteItem(obj);
        }
    }

    @FXML
    public void deleteOnKeyPressed (KeyEvent keyEvent) {
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
