package br.com.pedroaugusto.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.pedroaugusto.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request) {
    Object userId = request.getAttribute("userId");
    task.setUserId((UUID) userId);

    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())) {
      return ResponseEntity.badRequest().body("A data de início / termíno precisa ser maior que a data atual");
    } else if (task.getStartAt().isAfter(task.getEndAt())) {
      return ResponseEntity.badRequest().body("A data de início deve ser menor que a data de termíno");
    }

    TaskModel taskCreated = this.taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    Object userId = request.getAttribute("userId");

    List<TaskModel> tasks = this.taskRepository.findByUserId((UUID) userId);
    return tasks;
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel task, HttpServletRequest request, @PathVariable UUID id) {
    TaskModel taskOnDatabase = this.taskRepository.findById(id).orElse(null);

    if (taskOnDatabase == null) {
      return ResponseEntity.notFound().build();
    }

    Utils.copyNonNullableProperties(task, taskOnDatabase);

    TaskModel taskUpdated = this.taskRepository.save(taskOnDatabase);
    return ResponseEntity.ok().body(taskUpdated);
  }
}
