package com.example.sampletodolist;

import com.example.sampletodolist.DataModel.TodoData;
import com.example.sampletodolist.DataModel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private TextArea detailsArea;
    @FXML
    private TextField shortDescriptionField;

    public TodoItem processResults() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();

        TodoItem newItem = new TodoItem(shortDescription, details, deadlineValue);
        TodoData.getInstance().addTodoItem(newItem);
        //Dùng cho các video trước video 26
        //TodoData.getInstance().addTodoItem(new TodoItem(shortDescription, details, deadlineValue));
        return newItem;
    }


}
