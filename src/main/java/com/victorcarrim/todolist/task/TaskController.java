package com.victorcarrim.todolist.task;

import com.victorcarrim.todolist.user.IUserRepository;
import com.victorcarrim.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        taskModel.setIdUser((UUID) request.getAttribute("idUser"));

        var currentDate = LocalDateTime.now();

        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de inicio/termino não pode ser anterior a data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de inicio deve ser menor que a data de termino");
        }

        var task = this.taskRepository.saveAndFlush(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    @GetMapping("/getTaskByIdUser")
    public ResponseEntity<List<TaskModel>> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var list = this.taskRepository.findByIdUser((UUID) idUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(list);
    }

    @PutMapping("/{idTask}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID idTask){
        var idUser = request.getAttribute("idUser");
        var task =  this.taskRepository.findById(idTask).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task não encontrada"));
        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este usuário não tem permissão para alterar essa task");
        }
        Utils.copyNonNullProperties(taskModel, task);
        this.taskRepository.saveAndFlush(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(task);
    }
}
