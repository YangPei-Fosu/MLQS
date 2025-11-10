package io.mlqs.init;

import io.mlqs.memory.MemoryService;
import io.mlqs.memory.db.MemoryEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping()
public class initController {

    @Resource
    private MemoryService memoryService;

    // 查询所有有效数据
    @GetMapping("/all")
    public List<MemoryEntity> getAllValid() {
        return memoryService.getAllValid();
    }
}