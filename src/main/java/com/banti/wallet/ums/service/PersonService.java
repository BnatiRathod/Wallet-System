package com.banti.wallet.ums.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.banti.wallet.ums.elasticsearch.repositories.ElasticPersonRepository;
import com.banti.wallet.ums.enums.PersonStatus;
import com.banti.wallet.ums.model.Person;
import com.banti.wallet.ums.elasticsearch.baseInterfaces.BasePerson;
import com.banti.wallet.ums.elasticsearch.models.ElasticPerson;
import com.banti.wallet.ums.repository.PersonRepository;
import com.banti.wallet.ums.requestEntities.PersonRequestEntity;


@Service
@Transactional
public class PersonService extends BasePerson {
	
	@Autowired
	private ElasticPersonRepository elasPersonRepository;
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
    @Autowired
    private PersonRepository personRepo;
    
    
    public ElasticPerson findByMobileNo(String mobileNo) {
       return elasPersonRepository.findByMobileNo(mobileNo);
    }
    
    public Iterable<ElasticPerson> listAllPerson() {
        return elasPersonRepository.findAll();
    }
    
    //TO UPDATE PERSON RECORD
    public void updatePerson(Person person) throws NoSuchElementException
    {
    	
    	 ElasticPerson elasaticPerson = elasPersonRepository.findById(person.getUserId()).get();
    	 
    	 elasaticPerson.setUserName(person.getUserName());                         
    	 elasaticPerson.setFirstName(person.getFirstName());                       
    	 elasaticPerson.setLastName(person.getLastName());                          
    	 elasaticPerson.setAddress(person.getAddress());
    	 elasaticPerson.setMobileNo(person.getMobileNo());
    	 elasPersonRepository.save( elasaticPerson);
    	 
    	 Person existUser =  personRepo.findById(person.getUserId()).get();
    	 
         existUser.setUserName(person.getUserName());                         
         existUser.setFirstName(person.getFirstName());                       
         existUser.setLastName(person.getLastName());                          
         existUser.setAddress(person.getAddress());
         existUser.setMobileNo(person.getMobileNo());
         personRepo.save(existUser);
         
    }
    
    //TO STORE A PERSON RECORD
    public void saveUser(PersonRequestEntity user) throws Exception
    {
    	
    	//FATCH FROM ELASTICSEARCH DATABASE
    	ElasticPerson existPerson = elasPersonRepository.findByMobileNo(user.getMobileNo());
        //Person existUser = personRepo.findByMobileNo(user.getMobileNo());
        if(existPerson!=null)
        	throw new Exception("person is already exist"); 
        
        Person realUser=new Person();
    	realUser.setPassword(bcryptEncoder.encode(user.getPassword()));         
    	realUser.setUserName(user.getUserName());
    	realUser.setAddress(user.getAddress());
    	realUser.setFirstName(user.getFirstName());
    	realUser.setLastName(user.getLastName());
    	realUser.setMobileNo(user.getMobileNo());
    	realUser.setEmail(user.getEmail());
    	realUser.setRegisterDate(new Date());
    	realUser.setStatus(PersonStatus.ACTIVE.name());
    	//TO SAVE RECORD IN MYSQL DATABASE
    	personRepo.save(realUser);
        
        ElasticPerson elasticPerson = new ElasticPerson();
        elasticPerson.setUserName(user.getUserName());
        elasticPerson.setAddress(user.getAddress());
        elasticPerson.setEmail(user.getEmail());
        elasticPerson.setFirstName(user.getFirstName());
        elasticPerson.setLastName(user.getLastName());
        elasticPerson.setMobileNo(user.getMobileNo());
        elasticPerson.setPassword(bcryptEncoder.encode(user.getPassword()));
        elasticPerson.setRegisterDate(new Date());
        elasticPerson.setStatus(PersonStatus.ACTIVE.name());
        // TO SAVE RECORD IN ELASTICSEARCH DATABASE
        elasPersonRepository.save(elasticPerson);
                                                                                              
    }
     
    public ElasticPerson getPerson(Long id) {
        return elasPersonRepository.findById(id).get();
    }
     
    public void deletePerson(Long id) throws NoSuchElementException {
    	personRepo.deleteById(id);
    }
    
    public Person findUserByUserName(String username)
    {
    	return personRepo.findUserByUserName(username);
    }
    

}