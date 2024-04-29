package com.example.sampletodolist;

import com.example.sampletodolist.DataModel.TodoData;
import com.example.sampletodolist.DataModel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HelloController {
  private List<TodoItem> todoItems;
  @FXML
  private TextArea itemDetailsTextArea;
  @FXML
  private ListView<TodoItem> todoListView;
  @FXML
  private Label deadLineLabel;
  @FXML
  private BorderPane mainBorderPane;
  @FXML
  private ContextMenu listContextMenu;
  @FXML
  private ToggleButton filterToggleButton;
  private FilteredList<TodoItem> filteredList;
  private Predicate<TodoItem> wantAllItems;
  private Predicate<TodoItem> wantTodaysItems;

  public void initialize() {
//      TodoItem item1 = new TodoItem("Leo chiến tướng","Mỗi ngày đánh 30 games", LocalDate.of(2024, Month.APRIL,30));
//      TodoItem item2 = new TodoItem("Leo thách đấu tft","Ko 5 top 1 ko off", LocalDate.of(2024, Month.APRIL,30));
//      TodoItem item3 = new TodoItem("Tập cầu lông","Mỗi ngày smash 30 trái", LocalDate.of(2024, Month.APRIL,29));
//      TodoItem item4 = new TodoItem("Học","30 bài code thiếu nhi mỗi tối", LocalDate.of(2024, Month.APRIL,29));
//      TodoItem item5 = new TodoItem("Mượn sách","Ra thư viện mượn sách", LocalDate.of(2024, Month.APRIL,28));
//
//      todoItems = new ArrayList<TodoItem>();
//      todoItems.add(item1);
//      todoItems.add(item2);
//      todoItems.add(item3);
//      todoItems.add(item4);
//      todoItems.add(item5);
//
//      TodoData.getInstance().setTodoItems(todoItems);
//  Sau khi ta tạo phương thức init trong class application thì không
// cần phải dùng cách nhập thủ công này nữa, nhưng tại vì trc khi học đến thì ta cần dùng nên
//  lúc cần ta sẽ xoá bôi đen đi dùng lại

      //video 29
      listContextMenu = new ContextMenu();
      MenuItem deleteMenuItem = new MenuItem("Delete");
      deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
              TodoItem item = todoListView.getSelectionModel().getSelectedItem();
              deleteItem(item);
          }
      });
      listContextMenu.getItems().add(deleteMenuItem);

      //Code dưới đây dùng để khi chạy chương trình nó sẽ ngay lập tức mở cho mình mục đầu tiên.
      todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
          @Override
          public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
              if (newValue != null) {
                  TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                  itemDetailsTextArea.setText(item.getDetails());
                  DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                  deadLineLabel.setText(df.format(item.getDeadline()));
              }
          }
      } );

      //Video32: filteredList
      wantAllItems = new Predicate<TodoItem>() {
          @Override
          public boolean test(TodoItem item) {
              return true;
          }
      };
      wantTodaysItems = new Predicate<TodoItem>() {
          @Override
          public boolean test(TodoItem item) {
              return (item.getDeadline().equals(LocalDate.now()));
          }
      };

//      filteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(),
//              new Predicate<TodoItem>() {
//                 @Override
//                  public boolean test(TodoItem todoItem) {
//                     return true;
//                 }
//              });

      //Video31: use an observable arraylist to store the todo list items /
      filteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems() , wantAllItems);

      SortedList<TodoItem> sortedList = new SortedList<TodoItem>(filteredList, //filteredList dùng cho video 32, Thay bằng TodoData.getInstance().getTodoItems() tại video 30
              new Comparator<TodoItem>() {
                  @Override
                  public int compare(TodoItem o1, TodoItem o2) {
                      return o1.getDeadline().compareTo(o2.getDeadline());
                  }
              });

      //todoListView.getItems().setAll(todoItems); // trc khi có hàm init ở application thì cần dùng
      //todoListView.getItems().setAll(TodoData.getInstance().getTodoItems()); // Dùng cho video 26 trở về trc
      //todoListView.setItems(TodoData.getInstance().getTodoItems()); // Dùng cho video 27, we can remove the code that explicitly populates the listview when the user adds a new todo item
      todoListView.setItems(sortedList); // Lời gọi để sắp xếp lại item theo trình tự tgian
      todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      todoListView.getSelectionModel().selectFirst();

      //Video28: tạo cell factories
      todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
          @Override
          public ListCell<TodoItem> call(ListView<TodoItem> param) {
              ListCell<TodoItem> cell = new ListCell<TodoItem>() {
                  @Override
                  protected void updateItem(TodoItem item, boolean empty) {
                      super.updateItem(item, empty);
                      if(empty) {
                          setText(null);
                      } else {
                          setText(item.getShortDescription());
                          if(item.getDeadline().equals(LocalDate.now())) {
                              //Cảnh báo rằng nay là hết hạn
                              setTextFill(Color.RED);
                          }else if(item.getDeadline().equals(LocalDate.now().plusDays(1))) {
                              //Cảnh báo rằng 1 ngày nữa là hết hạn
                              setTextFill(Color.GREEN);
                          };
                      }
                  }
              };

              cell.emptyProperty().addListener(
                      (obs, wasEmpty, isNowEmpty) -> {
                          if(isNowEmpty) {
                              cell.setContextMenu(null);
                          } else {
                              cell.setContextMenu(listContextMenu);
                          }
                      });   // anonymous function hay là lamda expression

              return cell;
          }
      });
  }

  //Method show new item dialogue
    public void showNewItemDialog() {
      Dialog<ButtonType> dialog = new Dialog<>();
      dialog.initOwner(mainBorderPane.getScene().getWindow());
      dialog.setTitle("Add new Todo item");
      dialog.setHeaderText("Use this dialog to create a new item");
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));

      try {
          dialog.getDialogPane().setContent(fxmlLoader.load()); //video 25
          // Dùng cho video 24
          //  Parent root = FXMLLoader.load(getClass().getResource("todoItemDialog.fxml"));
          //  dialog.getDialogPane().setContent(root);
      } catch(IOException e) {
          System.out.println("Couldn't load the dialog");
          e.printStackTrace();
          return;
      }
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
      Optional<ButtonType> result = dialog.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
          DialogController controller = fxmlLoader.getController();
          TodoItem newItem = controller.processResults();
          //controller.processResults(); // Dùng cho video 25
         // todoListView.getItems().setAll(TodoData.getInstance().getTodoItems()); //Lệnh này giúp ta khi thêm todoItem mới thì sẽ hiện ở trang main luôn mà không cần phải tắt đi bật lại app.
                                                                                   // Sau khi dùng observable thì ko cần cái này nữa
          todoListView.getSelectionModel().select(newItem);
          System.out.println("OK pressed");
      } else {
          System.out.println("Cancel pressed");
      }
    }

  // Cài đặt ấn nút D thì sẽ xoá Item đang chọn
  @FXML
  public void handleKeyPressed(KeyEvent keyEvent) {
      TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
      if(selectedItem != null) {
          if(keyEvent.getCode().equals(KeyCode.D)) {
              deleteItem(selectedItem);
          }
      }
  }

  @FXML
  public void handleClickListView(MouseEvent mouseEvent) {
      TodoItem item = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
    //  System.out.println("The selected item is: " + item);
      itemDetailsTextArea.setText(item.getDetails());
      deadLineLabel.setText(item.getDeadline().toString());
//      StringBuffer sb = new StringBuffer(item.getDetails());
//      sb.append("\n\n\n\n\n");
//      sb.append("Hạn đến hết ngày: ");
//      sb.append(item.getDeadline().toString());
//     itemDetailsTextArea.setText(sb.toString());
// Phần bôi đen này là cách thủ công
    }

  @FXML
  public void deleteItem(TodoItem item) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete Todo Item");
      alert.setHeaderText("Delete item: " + item.getShortDescription());
      alert.setContentText("Are you sure? Press OK to confirm, or CANCEL to back out." );
      Optional<ButtonType> result = alert.showAndWait();

      if(result.isPresent() && result.get() == ButtonType.OK) {
          TodoData.getInstance().deleteTodoItem(item);
      }
  }

  //Video32: tạo 1 controller method để ấn vào sẽ hiện ra các item hết hạn vào hôm nay
  public void handleFilterButton(){
      TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
      if(filterToggleButton.isSelected()) {
//Dùng cho video 31
//          filteredList.setPredicate(new Predicate<TodoItem>() {
//              @Override
//              public boolean test(TodoItem todoItem) {
//                  return (todoItem.getDeadline().equals(LocalDate.now()));
//              }
//          });
          filteredList.setPredicate(wantTodaysItems);
          if(filteredList.isEmpty()) {
              itemDetailsTextArea.clear();
              deadLineLabel.setText("");
          } else if(filteredList.contains(selectedItem)) {
              todoListView.getSelectionModel().select(selectedItem);
          } else {
              todoListView.getSelectionModel().selectFirst();
          }
      } else {
//         //dùng cho video 31
//         filteredList.setPredicate(new Predicate<TodoItem>() {
//             @Override
//             public boolean test(TodoItem todoItem) {
//                 return true;
//             }
//         });
          filteredList.setPredicate(wantAllItems);
          todoListView.getSelectionModel().select(selectedItem);
      }
  }

  @FXML
   public void handleExit() {
      Platform.exit();
  }
}