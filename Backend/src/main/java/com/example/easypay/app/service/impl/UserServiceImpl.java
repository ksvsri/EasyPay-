package com.example.easypay.app.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.easypay.app.dto.request.ConfirmUpiRequestDto;
import com.example.easypay.app.dto.request.OtpValidateDto;
import com.example.easypay.app.dto.request.RegisterDto;
import com.example.easypay.app.dto.request.RemoveLinkedAccountRequestDto;
import com.example.easypay.app.dto.request.ResetUpiPinRequestDto;
import com.example.easypay.app.dto.request.SelectedAccountForLink;
import com.example.easypay.app.dto.request.SetPrimaryAccountRequestDto;
import com.example.easypay.app.dto.request.UpdateUserDetails;
import com.example.easypay.app.dto.response.LinkedAccountResponse;
import com.example.easypay.app.dto.response.UserProfileResponseDto;
import com.example.easypay.app.entity.AppUser;
import com.example.easypay.app.entity.Notification;
import com.example.easypay.app.entity.ReferralReward;
import com.example.easypay.app.entity.UserLinkedAccount;
import com.example.easypay.app.entity.UserProperties;
import com.example.easypay.app.enums.NotificationEntityType;
import com.example.easypay.app.enums.NotificationType;
import com.example.easypay.app.enums.UserLinkedAccountStatus;
import com.example.easypay.app.mapper.RegisterMapper;
import com.example.easypay.app.mapper.UserLinkedAccountMapper;
import com.example.easypay.app.repository.AppUserRepository;
import com.example.easypay.app.repository.NotificationRepository;
import com.example.easypay.app.repository.ReferralRewardRepository;
import com.example.easypay.app.repository.UserLinkedAccountRepository;
import com.example.easypay.app.service.AuthService;
import com.example.easypay.app.service.UserService;
import com.example.easypay.bank.dto.response.AccountResponse;
import com.example.easypay.bank.entity.BankAccount;
import com.example.easypay.bank.entity.BankUpiIds;
import com.example.easypay.bank.repository.BankUpiIdsRepository;
import com.example.easypay.bank.service.AccountService;
import com.example.easypay.bank.service.BankService;
import com.example.easypay.config.security.CustomUserDetails;
import com.example.easypay.config.security.CustomUserDetailsService;
import com.example.easypay.exceptions.ExistException;
import com.example.easypay.exceptions.InvalidOtpException;
import com.example.easypay.exceptions.RecordNotFoundException;
import com.example.easypay.exceptions.UnableToCreateException;
import com.example.easypay.util.JwtUtil;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private RegisterMapper registerMapper;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private BankUpiIdsRepository bankUpiIdsRepository;
	@Autowired
	private ReferralRewardRepository referralRewardRepository;
	
	@Autowired
	private CustomUserDetailsService customerUserDetailsService;
	
	@Autowired
	private UserLinkedAccountRepository userLinkedAccountRepository;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserLinkedAccountMapper userLinkedAccountMapper;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private BankService bankService;
	@Autowired
	private AuthService authService;

	@Autowired
	private JwtUtil jwtUtil;


	
	public AppUser getAppUser(Integer userId) throws RecordNotFoundException {
	     return	appUserRepository.findById(userId)
       .orElseThrow(() -> new RecordNotFoundException("User not found"));
	}
  
	/**
	 *  Register the user.
	 *  
	 *  Constraints: Checks user not registered previously neither with mobile or email.
	 *               Checks user enters correct OTP
	 *               Checks user registered using referral code or not
	 *               
	 */
	@Override
	public String registerUser(RegisterDto registerDto)
	        throws RecordNotFoundException, InvalidOtpException, ExistException {

		// Creating dto to validate the OTP
	    OtpValidateDto dto = new OtpValidateDto();
	    dto.setMobileNo(registerDto.getUserMobileNumber());
	    dto.setReceivedOtp(registerDto.getReceivedOtp());


	    // Checking user exists with the email or not, if exists throws exception
	    // Mobile number exists checked while generate the OTP
	    if (appUserRepository.findByUserEmail(registerDto.getUserEmail().toLowerCase().trim()).isPresent()) {
	        throw new ExistException("User already exists with Email " + registerDto.getUserEmail());
	    }

	    // Verifies the OTP
	    if (!authService.validateOtp(dto)) {
	        throw new InvalidOtpException("Invalid OTP");
	    }

	    //Mapping from DTO to Entity
	    AppUser appUser = registerMapper.toAppUser(registerDto);
	    
	    // Storing email as lowercase to avoid the case sensitive comparision
	    //errors for unique mail constraints
	    appUser.setUserEmail(registerDto.getUserEmail().toLowerCase().trim());
	    
	    // Generating the referral code for the user
	    String generatedReferralCode =
	            UUID.randomUUID().toString().replace("-", "").substring(0, 6);

	    // Setting user Properties 
	    UserProperties userProperties = new UserProperties();
	    userProperties.setAppLock(registerDto.getAppLock());
	    userProperties.setReferralCode(generatedReferralCode);
	    appUser.setUserProperties(userProperties);
	    userProperties.setAppUser(appUser);

	    String usedReferralCode = registerDto.getReferralCode();

	    //Checking user registered without the referral or with the referral
	    
	    boolean check=true;
	    if (usedReferralCode != null && !usedReferralCode.isBlank()) {

	    	// Get the referrer
	        AppUser referrerUser =
	                appUserRepository.findByUserProperties_ReferralCode(usedReferralCode)
	                        .orElseThrow(() ->
	                                new RecordNotFoundException("Invalid referral code"));
	        
	        // Get how many this referrer referred
	        // To increase the price value each time he referred.
	        
	        Integer count =
	                referralRewardRepository.countByReferrerUser_AppUserId(
	                        referrerUser.getAppUserId()
	                );

	        System.out.println("Referral User count "+count);
	        
	        // Creating referral entity
	        ReferralReward reward = new ReferralReward();
	        
	        // Referrer User
	        reward.setReferrerUser(referrerUser); 
	        
	        //Referred User
	        reward.setReferredUser(appUser); 
	        
	        // Award as 10+count*5
	        BigDecimal rewardAmount =
	                BigDecimal.valueOf(10)
	                          .add(BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(5)));

	        reward.setRewardAmount(rewardAmount);
	        reward.setCreatedAt(LocalDateTime.now());
	        appUserRepository.save(appUser);
	        
	        // Here saved no need to save again in line no(180)
	        check=false;
	        referralRewardRepository.save(reward);
	    }
	    if(check)
	    appUserRepository.save(appUser);
	    return "Registration completed successfully";
	}

	

	/**
	 *  Login the user and return the token
	 */
	@Override
	public String login(OtpValidateDto otpValidateDto) throws InvalidOtpException {

		// Verifies the OTP 
		if (authService.validateOtp(otpValidateDto)) {

			// if success get the user details using service that contains role and name 
			CustomUserDetails userDetails =
			        (CustomUserDetails) customerUserDetailsService
			                .loadUserByUsername(otpValidateDto.getMobileNo());

			String role = userDetails.getAuthorities()
			        .iterator()
			        .next()
			        .getAuthority();
			
			// generate the token
			String token = jwtUtil.generateToken(userDetails.getUsername(),userDetails.getUserId(),role);
			return token;
					

		}
		return null;
	}

	
	/**
	 * UC-01-- CREATING AND MANAGING USER PROFILE
	 *  Return the user Profile as response
	 */
	@Override
	public UserProfileResponseDto getProfile(Integer userId) throws RecordNotFoundException {
		
		// Get the user Using Id
		AppUser appUser=getAppUser(userId);
		
		//Mapping from entity to DTO
		UserProfileResponseDto responseDto=new UserProfileResponseDto();
		responseDto.setMobileNo(appUser.getUserMobileNumber());
		responseDto.setUserEmail(appUser.getUserEmail());
		responseDto.setUserName(appUser.getUserName());
		responseDto.setProfileImagePath(appUser.getProfileImagePath());
		responseDto.setLinkedAccounts(getLinkedAccounts(userId));
		responseDto.setCreatedAt(appUser.getCreatedAt());
		return responseDto;
	}



	/**
	 * UC-02--- ACCOUNT LINK AND CREATION
	 *  Linking an account to user 
	 * @throws UnableToCreateException 
	 */
	@Override
	public String LinkAccount(SelectedAccountForLink dto) throws RecordNotFoundException, ExistException, UnableToCreateException {
		 
		//  Validate user
	    AppUser user =getAppUser(dto.getUserId());

	    //  Validate bank account
	    BankAccount bankAccount = accountService.accountExistsOrNot(dto.getAccountId());
	    
	    // Setting notification entity
	    Notification notification=new Notification();
	    notification.setAppUser(user);
	    notification.setEntityType(NotificationEntityType.ACCOUNT_LINK);
	    notification.setEntityId(bankAccount.getBankAccountId());
	    notification.setIsRead(false);
	    
	    AccountResponse accountResponse=accountService.getAccountById(dto.getAccountId());
	    //  Check if already linked
	    if (userLinkedAccountRepository
	            .existsByBankAccountIdAndStatus(
	                    dto.getAccountId(),
	                    UserLinkedAccountStatus.ACTIVE
	            )) {
	    	
	    	// Operation failed notification message and save
	    	notification.setType(NotificationType.FAILED);
	    	notification.setMessage("This account ending with"+accountResponse.getMaskedAccountNo() +" is already linked!");
	    	notificationRepository.saveAndFlush(notification);
	    	
	        throw new ExistException(
	                "This bank account is already linked");
	    }

	    //Check UPI existence second step validation
	    BankUpiIds upi = bankUpiIdsRepository
	            .findByBankAccount_BankAccountId(dto.getAccountId())
	            .orElseThrow(() -> {
	            	
	            	// Operation failed notification message and save
	                notification.setType(NotificationType.FAILED);
	                notification.setMessage("Account ending with "+accountResponse.getMaskedAccountNo() +" UPI not created for this account!");
	                notificationRepository.saveAndFlush(notification);

	                return new RecordNotFoundException(
	                    "UPI not created for this account"
	                );
	            });
	    
	    //If user is already linked but status as removed,then convert to active.
	    Optional<UserLinkedAccount> removedOpt =
	            userLinkedAccountRepository.findByBankAccountIdAndStatus(
	                    dto.getAccountId(),
	                    UserLinkedAccountStatus.REMOVED
	            );

	    if (removedOpt.isPresent()) {
	        UserLinkedAccount existing = removedOpt.get();

	        existing.setStatus(UserLinkedAccountStatus.ACTIVE);
	        existing.setPrimary(
	                !userLinkedAccountRepository
	                        .existsByAppUser_AppUserIdAndIsPrimaryTrue(dto.getUserId())
	        );

	        userLinkedAccountRepository.save(existing);

	        notification.setType(NotificationType.SUCCESS);
	        notification.setMessage(
	                "Bank account ending with "
	                + accountResponse.getMaskedAccountNo()
	                + " linked successfully"
	        );
	        notificationRepository.saveAndFlush(notification);

	        return "Bank account linked successfully";
	    }
             

	    // Creating linked account entity reference
	    UserLinkedAccount link = new UserLinkedAccount();
	    link.setAppUser(user);
	    link.setBankAccountId(bankAccount.getBankAccountId());
	    link.setBankId(bankAccount.getBranch().getBank().getBankId());
	    link.setBankName(bankAccount.getBranch().getBank().getBankName());
	    link.setAccountType(bankAccount.getAccountType());
	    link.setUpiId(upi.getUpiId());
	    link.setMaskedAccountNo(
	            accountService.maskAccountNo(bankAccount.getAccountNumber())
	    );
	    
	    //boolean value whether pin is set or not
	    link.setPinSet(
	    	    Optional.ofNullable(upi.getUpiPinHash())
	    	            .filter(pin -> !pin.isBlank())
	    	            .isPresent()
	    	);

	    // Primary logic
	    boolean hasPrimary =
	            userLinkedAccountRepository
	                    .existsByAppUser_AppUserIdAndIsPrimaryTrue(dto.getUserId());

	    link.setPrimary(!hasPrimary);

	    userLinkedAccountRepository.save(link);
	    
	 // Operation success notification message and save
	    notification.setType(NotificationType.SUCCESS);
    	notification.setMessage("Bank account ending with "+accountResponse.getMaskedAccountNo()+ " linked successfully");
    	notificationRepository.saveAndFlush(notification);
	    
	    return "Bank account linked successfully";
	}



	/**
	 * UC-01---  CREATING AND MANAGING USER PROFILE
	 * Return the linked accounts to the user.
	 */
	@Override
	public List<LinkedAccountResponse> getLinkedAccounts(Integer userId) {
		 List<UserLinkedAccount> linkedAccounts =
		            userLinkedAccountRepository.findByAppUser_AppUserIdAndStatus(
		                    userId,
		                    UserLinkedAccountStatus.ACTIVE
		            );

		    return linkedAccounts.stream()
		            .map(userLinkedAccountMapper::toDto)
		            .toList();
		}



	/**
	 *  UC-02--ACCOUNT LINK AND CREATION
	 *  If user doesn't has UPI Id, this will confirm the UPI id
	 *  Adds a record to the UpiId entity.
	 */
	@Override
	public String confirmUpi(ConfirmUpiRequestDto dto) throws RecordNotFoundException, UnableToCreateException {
		
		// validate the user
		AppUser user=getAppUser(dto.getUserId());
		
		//Creating the notification entity
		Notification notification=new Notification();
	    notification.setAppUser(user);
	    notification.setEntityType(NotificationEntityType.CREATE_UPI);
	    notification.setEntityId(dto.getAccountId());
	    notification.setIsRead(false);
	    
	    // Getting account details using account id
	    AccountResponse accountResponse=accountService.getAccountById(dto.getAccountId());
        
	    // Handling exception instead of throwing from the bank itself
	    
	    try {
	    	//calling bank service to confirm the upi
	    	notification.setType(NotificationType.SUCCESS);
            notification.setMessage("UPI Id created successfully for bank account having the bank account ending with "
			+accountResponse.getMaskedAccountNo()+". Now you can link your account. ");
            notificationRepository.saveAndFlush(notification);
	    	bankService.confirmUpi(dto.getAccountId(),dto.getUpiId(),dto.getUpiPin());
	    	
	    	
            return "UPI ID created successfully";
	    }
		
	    //Handling exception from the bank to set failed notification status
	    catch(UnableToCreateException e) {
	    	notification.setType(NotificationType.FAILED);
	    	notification.setMessage(e.getMessage());
            notificationRepository.saveAndFlush(notification);
           throw e;
	    }
		 
	}


	/**
	 * UC-01--- CREATING AND MANAGING USER PROFILE
	 *  Updates the user details
	 */
	@Override
	public String updateUserDetails(UpdateUserDetails dto) throws RecordNotFoundException, ExistException {
		
		// validate the user
		AppUser user=getAppUser(dto.getUserId());
		
		// check current and new name changed or not
		if(!user.getUserName().equalsIgnoreCase(dto.getUserName()))
			user.setUserName(dto.getUserName());
		
		//Check new email is not in the database.
		if(!user.getUserEmail().equalsIgnoreCase(dto.getUserEmail())) {
			Optional<AppUser> userBasedOnChangedEmail=appUserRepository.findByUserEmail(dto.getUserEmail());
			if(userBasedOnChangedEmail.isPresent()) 
				throw new ExistException("User already exists with the provided email");
			else {
				user.setUserEmail(dto.getUserEmail());
			}
		}
		appUserRepository.save(user);
		return "Updated successfully";
	}


	/**
	 * UC-01- CREATING AND MANAGING USER PROFILE
	 * Mark an account as primary 
	 * @throws UnableToCreateException 
	 */

	@Override
	@Transactional
	public String markAccountAsPrimary(SetPrimaryAccountRequestDto dto)
	        throws RecordNotFoundException, UnableToCreateException {

	    // 1️ Validate user
	    AppUser user = getAppUser(dto.getUserId());

	    // 2️ Find previous primary 
	    UserLinkedAccount prevPrimary =
	            userLinkedAccountRepository
	                    .findByAppUser_AppUserIdAndIsPrimaryTrue(dto.getUserId())
	                    .orElse(null);

	    // Creating notification entity
	    Notification notification = new Notification();
	    notification.setAppUser(user);
	    notification.setEntityType(NotificationEntityType.PRIMARY);
	    notification.setEntityId(dto.getNewPrimaryAccountId());
	    notification.setIsRead(false);

	    // Find new primary
	    UserLinkedAccount newPrimary =
	            userLinkedAccountRepository
	                    .findByBankAccountId(dto.getNewPrimaryAccountId())
	                    .orElseThrow(() -> {

	                        notification.setType(NotificationType.FAILED);
	                        notification.setMessage("Account does not exist!");
	                        notificationRepository.save(notification);

	                        return new RecordNotFoundException("Account does not exist");
	                    });

	    // calling bank service to update the bank side and handling exception
	    try {
	        bankService.setAccountAsPrimary(
	                prevPrimary == null ? null : prevPrimary.getBankAccountId(),
	                newPrimary.getBankAccountId()
	        );

	        // to update from the app side
	        if (prevPrimary != null) {
	            prevPrimary.setPrimary(false);
	        }
	        newPrimary.setPrimary(true);

	        userLinkedAccountRepository.saveAll(
	                prevPrimary == null
	                        ? List.of(newPrimary)
	                        : List.of(prevPrimary, newPrimary)
	        );

	        AccountResponse accountResponse =
	                accountService.getAccountById(dto.getNewPrimaryAccountId());

	        notification.setType(NotificationType.SUCCESS);
	        notification.setMessage(
	                "Account ending with " + accountResponse.getMaskedAccountNo() +
	                " is changed to Primary account."
	        );
	        notificationRepository.save(notification);

	        return "Account marked as primary";

	    } catch (RecordNotFoundException e) {

	        notification.setType(NotificationType.FAILED);
	        notification.setMessage(e.getMessage());
	        notificationRepository.save(notification);

	        throw e;
	    }
	}


	/**
	 * UC-01-- CREATING AND MANAGING USER PROFILE
	 * Remove account from the user linked accounts not from the bank.
	 * @throws UnableToCreateException 
	 */

	@Override
	@Transactional
	public String removeAccount(RemoveLinkedAccountRequestDto dto) throws RecordNotFoundException, UnableToCreateException {
		
		// check user exists or not
		AppUser user= getAppUser(dto.getUserId());
		// Check account exists or not
		UserLinkedAccount removeAccount=userLinkedAccountRepository.findByBankAccountId(dto.getAccountId())
				.orElseThrow(()->new RecordNotFoundException("Account Doesnot exist"));
		
		Notification notification = new Notification();
	    notification.setAppUser(user);
	    notification.setEntityType(NotificationEntityType.ACCOUNT_UNLINK);
	    notification.setEntityId(dto.getAccountId());
	    notification.setIsRead(false);
	    notification.setType(NotificationType.SUCCESS);
	    AccountResponse accountResponse=accountService.getAccountById(dto.getAccountId());
        notification.setMessage(
                "Account ending with " + accountResponse.getMaskedAccountNo() +
                " is unlinked successfully"
        );
        notificationRepository.save(notification);
        
		// Soft delete
        removeAccount.setStatus(UserLinkedAccountStatus.REMOVED);
        removeAccount.setUpdatedAt(LocalDateTime.now());
        userLinkedAccountRepository.save(removeAccount);
		return "Unlinked account successfully";
	}



	/**
	 * UC-01---- CREATING AND MANAGING USER PROFILE
	 *    Update the user Uploaded image in the db
	 */
	@Override
	public String uploadProfileImage(MultipartFile image,Integer userId) throws IOException {
	    
		//  check image is empty or not 
	    if (image.isEmpty()) {
	        throw new RuntimeException("Image file is empty");
	    }

	    // Generate file name
	    String fileName = "user_" + userId + "_" + image.getOriginalFilename();

	    // Save to disk
	    Path uploadPath = Paths.get("uploads/profile-images");
	    Files.createDirectories(uploadPath);

	    Path filePath = uploadPath.resolve(fileName);
	    Files.write(filePath, image.getBytes());

	    //  Public path
	    String publicPath = "/profile-images/" + fileName;

	    // Update DB
	    AppUser user = appUserRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    user.setProfileImagePath(publicPath);
	    
	    Notification notification = new Notification();
	    notification.setAppUser(user);
	    notification.setEntityType(NotificationEntityType.PROFILE_IMAGE_UPDATE);
	    notification.setEntityId(userId);
	    notification.setIsRead(false);
	    notification.setType(NotificationType.SUCCESS);
        notification.setMessage("Profile image uploaded successfully.	");
        notificationRepository.save(notification);
	    appUserRepository.save(user);
	    
	    return "Updated successfully";
	}

	/**
	 * UC-01-- CREATING AND MANAGING USER PROFILE
	 * Resets the UPI pin of particluar account
	 */
	@Override
	public String resetUpiPin(ResetUpiPinRequestDto dto) throws RecordNotFoundException, UnableToCreateException {
		
		//Check user existence
		getAppUser(dto.getUserId());
		
		//App will not reset, request will be forwarded to the bank.
		boolean status=accountService.resetUpiPin(dto.getBankAccountId(),dto.getNewPin());
		
		if(status) return "UPI PIN updated successfully";
		
		return "Unable to reset the UPI PIN";
	}
	
	
}


