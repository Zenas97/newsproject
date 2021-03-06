package diego.newsproject;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import entities.Articoli;

@RestController
@RequestMapping("articoli")
@Transactional
public class restController 
{
	@Autowired
	ArticoliRepository articoliRepo;
	
	@ResponseBody
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Articoli> inserisciArticolo(@Valid @RequestBody(required = true) Articoli articolo) throws URISyntaxException
	{
		if(articoliRepo.save(articolo) == null) return ResponseEntity.internalServerError().build();
		return ResponseEntity.created(new URI("http://localhost:8080/articoli/" + articolo.getId())).build();
	}
	
	@GetMapping(path = "{id}")
	@ResponseBody
	public Articoli getArticolo(@PathVariable("id") Long id) {
		
		Articoli articolo = articoliRepo.findById(id).get();
		return articolo;
	}
	
	
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(NoSuchElementException.class)
	public String handleNoSuchElementException(NoSuchElementException ex) {
		return ex.getMessage();
	}
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String,String> handleValidationException(MethodArgumentNotValidException ex){
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getAllErrors().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();
	        errors.put(fieldName, errorMessage);
	    });
	    return errors;
	}

}
