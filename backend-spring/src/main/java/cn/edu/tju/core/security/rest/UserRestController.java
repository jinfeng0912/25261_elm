package cn.edu.tju.core.security.rest;

import cn.edu.tju.core.model.Authority;
import cn.edu.tju.core.model.Person;
import cn.edu.tju.core.security.SecurityUtils;
import cn.edu.tju.core.security.UserModelDetailsService;
import cn.edu.tju.core.security.repository.AuthorityRepository;
import cn.edu.tju.core.security.repository.PersonRepository;
import cn.edu.tju.core.security.repository.UserRepository;
import cn.edu.tju.core.security.rest.dto.LoginDto;
import cn.edu.tju.core.security.rest.dto.PasswordChangeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.service.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Tag(name = "管理用户", description = "提供用户的增删改查操作")
public class UserRestController {

   private final UserService userService;
   private final PersonRepository personRepository;
//   private final UserDetailsPasswordService userDetailsPasswordService;
   private final UserModelDetailsService userModelDetailsService;
   private final UserRepository userRepository;
   private final AuthorityRepository authorityRepository;
   private final PasswordEncoder passwordEncoder;

   public UserRestController(UserService userService, PersonRepository personRepository, UserModelDetailsService userModelDetailsService, UserRepository userRepository,AuthorityRepository authorityRepository,PasswordEncoder passwordEncoder) {
      this.userService = userService;
      this.personRepository = personRepository;
       this.userModelDetailsService = userModelDetailsService;
      this.userRepository = userRepository;
      this.authorityRepository=authorityRepository;
      this.passwordEncoder=passwordEncoder;
   }

   @PostMapping("/users")
   @Operation(summary = "新增用户（仅登录帐号）", description = "创建一个新的用户（仅登录帐号）")
   public ResponseEntity<User> createUser(@RequestBody User newUser) {
      // 从 newUser 对象中获取瞬时的 Authority 对象集合
      Set<Authority> transientAuthorities = newUser.getAuthorities();

      // 根据权限名称从数据库查出持久化的 Authority 对象
      Set<Authority> persistentAuthorities = transientAuthorities.stream()
              .map(authority -> authorityRepository.findById(authority.getName()).orElseThrow(() -> new RuntimeException("Authority not found: " + authority.getName())))
              .collect(Collectors.toSet());

      // 将持久化的 Authority 集合设置回 newUser 对象
      newUser.setAuthorities(persistentAuthorities);
      User user = userService.getUserWithAuthorities().get();

      LocalDateTime now = LocalDateTime.now();
      newUser.setCreator(user.getId());
      newUser.setCreateTime(now);
      newUser.setUpdater(user.getId());
      newUser.setUpdateTime(now);
      newUser.setDeleted(false);
      newUser.setPassword(SecurityUtils.BCryptPasswordEncode("password"));
      newUser.setActivated(true);
      User user1 = userService.addUser(newUser);
      return ResponseEntity.ok(user1);
   }

   @GetMapping("/user")
   @Operation(summary = "判断当前登录的用户", description = "判断当前登录的用户")
   public ResponseEntity<User> getActualUser() {
      return ResponseEntity.ok(userService.getUserWithAuthorities().get());
   }

   @PostMapping("/password")
   @Operation(summary = "修改密码", description = "已登录的用户只可以修改自己的密码，Admin可以修改任何人的密码")
   public ResponseEntity<String> updateUserPassword(@RequestBody LoginDto loginDto) {
      User me =  userService.getUserWithAuthorities().get();
      boolean isAdmin = false;
      for (Authority authority : me.getAuthorities()) {
          if (authority.getName().equals("ADMIN")) {
              isAdmin = true;
              break;
          }
      }
      //判断用户是否存在
      // 先查询用户，得到 Optional 对象
      Optional<User> updateUserOptional = userRepository.findOneWithAuthoritiesByUsername(loginDto.getUsername());

      //检查 Optional 是否为空
      if (updateUserOptional.isEmpty()) { // 或者使用 !updateUserOptional.isPresent()
         // 如果为空，说明用户不存在，直接返回一个明确的错误信息给前端
         return ResponseEntity.status(HttpStatus.NOT_FOUND)
                 .body("试图修改密码的用户 '" + loginDto.getUsername() + "' 不存在。");
      }

      // 确认用户存在后，再安全地获取 User 对象
      User updateUser = updateUserOptional.get();
      //userModelDetailsService.loadUserByUsername(loginDto.getUsername());
      if (me.getUsername().equals(loginDto.getUsername()) || isAdmin ) {
         //判断输入的旧密码是否正确，如果正确修改才可能成功
//         if(passwordChangeDTO.getOldPassword() == null ||!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), updateUser.getPassword())){
//            return ResponseEntity.badRequest().body("修改失败：旧密码不正确。");
//         }
         //验证两次输入的新密码是否一致
//         if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmNewPassword())) {
//            return ResponseEntity.badRequest().body("修改失败：两次输入的新密码不一致。");
//         }
         //没问题，修改成功
         updateUser.setPassword(SecurityUtils.BCryptPasswordEncode(loginDto.getPassword()));
         userService.updateUser(updateUser);
         return ResponseEntity.ok().body("Update the password successfully.");
      } else {
         return ResponseEntity.unprocessableEntity().body("Failed to update the password.");
      }
   }

   @PostMapping("/persons")
   @Operation(summary = "新增用户（自然人）", description = "创建一个新的用户（自然人）")
   public Person addPerson(@RequestBody Person person){
      User user = userService.getUserWithAuthorities().get();
      LocalDateTime now = LocalDateTime.now();
      person.setCreator(user.getId());
      person.setCreateTime(now);
      person.setUpdater(user.getId());
      person.setUpdateTime(now);
      person.setDeleted(false);
      person.setPassword(SecurityUtils.BCryptPasswordEncode("password"));
      person.setActivated(true);
      return personRepository.save(person);
   }

   @PostMapping("/register")
   @Operation(summary = "注册用户（自然人）", description = "注册一个新用户，无需认证即可使用")
   public ResponseEntity<?> register(@RequestBody Person person){
      // 检查用户名是否已存在
      if (userRepository.findOneWithAuthoritiesByUsername(person.getUsername()).isPresent()) {
         return ResponseEntity
                 .status(HttpStatus.BAD_REQUEST)
                 .body("错误：该用户名已被注册！");
      }
      String encryptedPassword = SecurityUtils.BCryptPasswordEncode("password");
      person.setPassword(encryptedPassword);
      LocalDateTime now = LocalDateTime.now();
      person.setCreateTime(now);
      person.setUpdateTime(now);
      person.setActivated(true);
      person.setDeleted(false);

      // 设置默认权限为 "USER"
      Authority userAuthority = authorityRepository.findById("USER")
              .orElseThrow(() -> new RuntimeException("错误：数据库中未找到 'USER' 权限。"));
      Set<Authority> authorities = new HashSet<>();
      authorities.add(userAuthority);
      person.setAuthorities(authorities);

      // 保存新用户到数据库
      Person savedPerson = personRepository.save(person);

      return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
   }
}
