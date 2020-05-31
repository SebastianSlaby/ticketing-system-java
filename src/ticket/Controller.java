package ticket;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;




public class Controller {
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private GridPane notesGridPane;
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
    @FXML
    private MenuItem menuItemNewNote;
    @FXML
    private CheckMenuItem hideTicketsMenuItem;

    private Ticket selectedTicket;
    private Predicate<Ticket> wantAllTickets;
    private Predicate<Ticket> wantUnsolvedTickets;
    private Predicate<Note> wantAllNotes;
    private Predicate<Note> wantNoNotes;
    private FilteredList<Ticket> filteredTicketList;
    private FilteredList<Note> filteredNoteList;
    private boolean haveNotesBeenInitialized = false;


    public void initialize () throws SQLException {

        wantAllTickets = new Predicate<Ticket>() {
            @Override
            public boolean test(Ticket ticket) {
                return true;
            }
        };
        wantUnsolvedTickets = new Predicate<Ticket>() {
            @Override
            public boolean test(Ticket ticket) {
                return (ticket.getStatusId() == 1);
            }
        };
        filteredTicketList = new FilteredList<Ticket>(Tickets.getInstance().getTickets(), wantAllTickets);

        ticketTableView.setItems(filteredTicketList);
        columnTicketName.setCellValueFactory(new PropertyValueFactory<Ticket, String>("ticketTitle"));
        columnTicketCreationDate.setCellValueFactory(new PropertyValueFactory<Ticket, LocalDate>("ticketDateOfCreation"));
        ticketTableView.getSortOrder().add(columnTicketCreationDate);
        ticketTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ticketTableView.getSelectionModel().selectFirst();
        initComboBox();
        showNotesIfResolver(isResolver(Account.getCurrentlyLoggedIn()));

        if(filteredTicketList.isEmpty()) {
            hideTicketsMenuItem.setDisable(true);
            menuItemNewNote.setVisible(false);
        }

        ticketTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ticket>() {
            @Override
            public void changed(ObservableValue<? extends Ticket> observableValue, Ticket oldValue, Ticket newValue) {
                if(newValue != null) {
                    selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
                    ticketTextArea.setText(selectedTicket.getTicketDetails());
                    filteredNoteList = new FilteredList<Note>(selectedTicket.getNotes().getNotes(), wantAllNotes);
                    noteTableView.setItems(filteredNoteList);
                    noteTableView.getSelectionModel().selectFirst();
                    if(noteTableView.getSelectionModel().getSelectedItem() == null) {
                        noteTextArea.setText("");
                    }
                    comboTicketState.getSelectionModel().select(selectedTicket.getStatusId()-1);
                }
            }
        });
        ticketTableView.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                if(ticketTableView.getItems().isEmpty()) {
                    ticketTextArea.clear();
                    noteTextArea.clear();
                    filteredNoteList.setPredicate(wantNoNotes);
                    buttonNewNote.setDisable(true);
                    comboTicketState.setDisable(true);
                    menuItemNewNote.setVisible(false);
                    if(!hideTicketsMenuItem.isSelected())
                        hideTicketsMenuItem.setDisable(true);
                } else {
                    buttonNewNote.setDisable(false);
                    hideTicketsMenuItem.setDisable(false);
                    comboTicketState.setDisable(false);
                    menuItemNewNote.setVisible(true);
                }
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
        columnTicketCreationDate.setCellFactory(new Callback<TableColumn<Ticket, LocalDate>, TableCell<Ticket, LocalDate>>() {
            @Override
            public TableCell<Ticket, LocalDate> call(TableColumn<Ticket, LocalDate> ticketLocalDateTableColumn) {
                TableCell<Ticket, LocalDate> cell = new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                    }
                };
                return cell;
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

    }

    public static boolean isResolver(int userId) throws SQLException {
        String isResolverSQLQuery = String.format("SELECT COUNT(*) FROM users WHERE id=%d AND user_type=%d;", userId, 2);
        int count = -1;
        try(
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement = connection.prepareStatement(isResolverSQLQuery);
                ResultSet resultSet = statement.executeQuery();
                ) {
            while(resultSet.next()) {
                count = resultSet.getInt(1);
            }
        }
        if(count > 0) return true;
        return false;
    }


    public void initializeNoteFields() {
        selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();

        wantAllNotes = new Predicate<Note>() {
            @Override
            public boolean test(Note note) {
                return true;
            }
        };
        wantNoNotes = new Predicate<Note>() {
            @Override
            public boolean test(Note note) {
                return false;
            }
        };

        if(selectedTicket.getNotes().getNotes() == null) {
            selectedTicket.getNotes().setNotes(FXCollections.observableArrayList());
        }
        filteredNoteList = new FilteredList<Note>(selectedTicket.getNotes().getNotes(), wantAllNotes);
        noteTableView.setItems(filteredNoteList);

        columnNoteName.setCellValueFactory(new PropertyValueFactory<Note, String>("noteTitle"));
        columnNoteCreationDate.setCellValueFactory(new PropertyValueFactory<Note, LocalDate>("noteDateOfCreation"));
        noteTableView.getSortOrder().add(columnNoteCreationDate);
        noteTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        noteTableView.getSelectionModel().selectFirst();
        if(noteTableView.getSelectionModel().getSelectedItem() != null) {
            Note selectedNote = (Note) noteTableView.getSelectionModel().getSelectedItem();
            noteTextArea.setText(selectedNote.getNoteDetails());
        }

        noteTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Note>() {
            @Override
            public void changed(ObservableValue<? extends Note> observableValue, Note oldValue, Note newValue) {
                if (newValue != null) {
                    Note note = (Note) noteTableView.getSelectionModel().getSelectedItem();
                    noteTextArea.setText(note.getNoteDetails());
                }
            }
        });

        noteTableView.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                if(filteredNoteList.isEmpty()) {
                    noteTextArea.clear();
                }
            }
        });

        noteTableView.setRowFactory(new Callback<TableView<Ticket>, TableRow<Ticket>>() {
            @Override
            public TableRow<Ticket> call(TableView tableView) {
                TableRow<Ticket> tableRow = new TableRow<>();

                tableRow.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
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
        comboTicketState.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {

                if (newValue != null) {
                    selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
                    if (comboTicketState.getSelectionModel().getSelectedIndex() + 1 != selectedTicket.getStatusId()) {
                        try {
                            selectedTicket.setStatusId(selectedTicket.getTicketId(), comboTicketState.getSelectionModel().getSelectedIndex() + 1);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if(hideTicketsMenuItem.isSelected()) {
                        hideClosedTickets();
                    }
                }
            }
        });
        haveNotesBeenInitialized = true;
    }

    public void showNotesIfResolver(Boolean bool) throws SQLException {

        if (!bool) {
            notesGridPane.getChildren().remove(noteTableView);
            menuItemNewNote.setVisible(false);
            notesGridPane.getChildren().remove(buttonNewNote);
            notesGridPane.getChildren().remove(noteTextArea);
            comboTicketState.setDisable(true);
            return;
        }else if (!filteredTicketList.isEmpty()) {
            initializeNoteFields();
        }
    }

    @FXML
    public void hideClosedTickets() {
        selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
        if(hideTicketsMenuItem.isSelected()) {
            filteredTicketList.setPredicate(wantAllTickets);
            filteredTicketList.setPredicate(wantUnsolvedTickets);
            if (filteredTicketList.contains(selectedTicket)) {
                ticketTableView.getSelectionModel().select(selectedTicket);
            } else {
                ticketTableView.getSelectionModel().selectFirst();
            }
        } else {
            filteredTicketList.setPredicate(wantAllTickets);
            filteredNoteList.setPredicate(wantAllNotes);
            if(selectedTicket != null) {
                ticketTableView.getSelectionModel().select(selectedTicket);
            } else {
                ticketTableView.getSelectionModel().selectFirst();
            }
            noteTableView.setDisable(false);
        }

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
            if(selectedTicket != null) {
                Note newNote = controller.processResults(selectedTicket);
                filteredNoteList = new FilteredList<Note>(selectedTicket.getNotes().getNotes(), wantAllNotes);
                noteTableView.setItems(filteredNoteList);
                noteTableView.getSelectionModel().select(newNote);
                noteTextArea.setText(newNote.getNoteDetails());
            }
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
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TicketDialogController controller = fxmlLoader.getController();
            Ticket newTicket = controller.processResults();

            ticketTableView.getSelectionModel().select(newTicket);
        }
        if (!haveNotesBeenInitialized) {
            try {
                showNotesIfResolver(isResolver(Account.getCurrentlyLoggedIn()));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(haveNotesBeenInitialized) {
            filteredNoteList.setPredicate(wantAllNotes);
        }
    }

    public void deleteItem (Object obj) throws SQLException {
        String doNotShowSQLQuery = String.format("SELECT confirm_delete FROM users WHERE id=%d;", Account.getCurrentlyLoggedIn());
        boolean hideAlert = false;
        try (
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement = connection.prepareStatement(doNotShowSQLQuery);
                ResultSet resultSet = statement.executeQuery();
                ) {
            while(resultSet.next()) {
                hideAlert = resultSet.getBoolean("confirm_delete");
            }
        }
        if(!hideAlert) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getDialogPane().applyCss();
            Node graphic = alert.getDialogPane().getGraphic();
            AtomicReference<Boolean> optOutAction = new AtomicReference<>(false);
            alert.setDialogPane(new DialogPane() {
                @Override
                protected Node createDetailsButton() {
                    CheckBox optOut = new CheckBox();
                    optOut.setText("Do not ask again");
                    optOut.setOnAction(e -> {
                        if(optOut.isSelected()) optOutAction.set(true);
                        else if(!optOut.isSelected()) optOutAction.set(false);
                    });
                    return optOut;
                }
            });
            alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            alert.getDialogPane().setExpandableContent(new Group());
            alert.getDialogPane().setExpanded(true);
            alert.getDialogPane().setGraphic(graphic);
            if(obj.getClass().equals(Note.class)) {
                alert.setTitle("Delete Resolver Note");
                alert.setHeaderText("Delete note: " + ((Note) obj).getNoteTitle());
            } else if (obj.getClass().equals(Ticket.class)) {
                alert.setTitle("Delete Ticket");
                alert.setHeaderText("Delete ticket: " + ((Ticket) obj).getTicketTitle());
            }
            alert.getDialogPane().setContentText("Are you sure?");
            alert.setResizable(false);
//            Optional<ButtonType> result = alert.showAndWait();
            if((alert.showAndWait().filter(t -> t == ButtonType.OK).isPresent())) {
                if(obj.getClass().equals(Note.class)) {
                    selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
                    selectedTicket.getNotes().deleteNote((Note) obj);
                }
                else if(obj.getClass().equals(Ticket.class))  {
                    Tickets.getInstance().deleteTicket((Ticket) obj);
                }
                if(optOutAction.get()) {
                    String changeToTrueSQLQuery = String.format("UPDATE users SET confirm_delete=%b WHERE id=%d;", optOutAction.get(), Account.getCurrentlyLoggedIn());
                    try (
                            Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                            PreparedStatement statement = connection.prepareStatement(changeToTrueSQLQuery);
                    ) {
                        statement.executeUpdate();
                    }
                }
            }
        } else {
            if(obj.getClass().equals(Note.class)) {
                selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
                selectedTicket.getNotes().deleteNote((Note) obj);
            }
            else if(obj.getClass().equals(Ticket.class))  {
                Tickets.getInstance().deleteTicket((Ticket) obj);
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

    public void initComboBox() {
        ObservableList<String> ticketStatus = FXCollections.observableArrayList();
        String getTicketStatusSQLQuery = String.format("SELECT name FROM ticket_status;");
        try(
                Connection connection =  new dbHandler("jdbc:mysql://localhost/ticketing?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","").connection;
                PreparedStatement statement = connection.prepareStatement(getTicketStatusSQLQuery);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while(resultSet.next()) {
                ticketStatus.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        comboTicketState.setItems(ticketStatus);
        selectedTicket = (Ticket) ticketTableView.getSelectionModel().getSelectedItem();
        if(selectedTicket != null) {
            comboTicketState.getSelectionModel().select(selectedTicket.getStatusId()-1);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }
}
