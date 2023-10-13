package com.victorcarrim.todolist.task;

import com.victorcarrim.todolist.user.IUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getTaskById")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        System.out.println(idUser);
        var list = this.taskRepository.findByIdUser((UUID) idUser);
        return list;
    }

    @PutMapping("/{idTask}")
    public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID idTask){
        taskModel.setId(idTask);
        this.taskRepository.saveAndFlush(taskModel);
        return taskModel;
    }
}
