package com.mytests.spring.sbmavenjavademo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringBootApplication
public class SbMavenJavaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbMavenJavaDemoApplication.class, args);
    }

}
@Entity
class SampleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }
}

interface SampleRepository extends CrudRepository<SampleEntity, Integer> {
    List<SampleEntity> findByDescriptionContaining(String description);
    @Query("select e from SampleEntity e where e.title = :title")
    List<SampleEntity> customQuery(@Param("title") String title);
}

@Service
class SampleService {
    private final SampleRepository repository;

    public SampleService(SampleRepository repository) {
        this.repository = repository;
    }

    public List<SampleEntity> findAll() {
        return (List<SampleEntity>) repository.findAll();
    }

    public SampleEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<SampleEntity> findByTitle(String title) {
        return repository.customQuery(title);
    }

    public List<SampleEntity> findByDescriptionContaining(String description) {
        return repository.findByDescriptionContaining(description);
    }
    @Transactional
    public SampleEntity save(SampleEntity entity) {
        return repository.save(entity);
    }

    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}

@RestController
@RequestMapping("/api/samples")
class SampleController {


    @Autowired
    private SampleService service;

    @GetMapping
    public List<SampleEntity> getAllSamples() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public SampleEntity getSampleById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @GetMapping("/search/title")
    public List<SampleEntity> getSamplesByTitle(@RequestParam String title) {
        return service.findByTitle(title);
    }

    @GetMapping("/search/description")
    public List<SampleEntity> getSamplesByDescription(@RequestParam String description) {
        return service.findByDescriptionContaining(description);
    }

    @PostMapping
    public SampleEntity createSample(@RequestBody SampleEntity entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void deleteSample(@PathVariable Integer id) {
        service.deleteById(id);
    }

    @EventListener
    public void on(ServletWebServerInitializedEvent event) {
        System.out.println("event: webserver " + event.getWebServer() + "started on " + event.getWebServer().getPort() + "!");
    }
}

