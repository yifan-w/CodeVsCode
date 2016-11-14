package com.codevscode.user;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;

@Controller
public class ConnectController extends org.springframework.social.connect.web.ConnectController {
	static Logger log = LoggerFactory.getLogger(ConnectController.class);
	@Inject
    public ConnectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
        super(connectionFactoryLocator, connectionRepository);

    }

    @Override
    protected String connectedView(String providerId) {
        return "redirect:/"+providerId;	
    }
    
    @Override
    protected String connectView(String providerId) {
		return "index";	
	}
    
    //TODO: There was an unexpected error (type=Internal Server Error, status=500).
    //PreparedStatementCallback; SQL [insert into users (name, email, passwordHash, friends, fid) values (?, ?, ?, ?, ?)]; Duplicate entry 'Keun Jun Park' for key 'name_UNIQUE'; 
    //nested exception is com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry 'Keun Jun Park' for key 'name_UNIQUE'
}