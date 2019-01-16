package  com.zq.springboot.redis.cache.democacheredis.web;
import java.util.List;

import com.zq.springboot.redis.cache.democacheredis.model.User;
import com.zq.springboot.redis.cache.democacheredis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/hello")
    @Cacheable(value="helloCache")
    public String hello(String name) {
        System.out.println("没有走缓存！");
        return "hello "+name;
    }

    @RequestMapping("/condition")
    @Cacheable(value="condition",condition="#name.length() <= 8" ,unless = "#result==null")
    public String condition(String name) {
        System.out.println("没有走缓存！");
        return "hello "+name;
    }

    @RequestMapping("/getUsers")
    @Cacheable(value="usersCache",key="#nickname",condition="#nickname.length() >= 5")
    public List<User> getUsers(String nickname) {
        List<User> users=userRepository.findByNickname(nickname);
        System.out.println("执行了数据库操作");
        return users;
    }

    /*先执行：http://localhost:8088/getUsers?nickname=smile 会执行方法
    再执行：http://localhost:8088/getPutUsers?nickname=smile 标记@CachePut的注解会执行方法之前不判断之前有无此缓存，而是直接执行方法后将返回结果放入缓存中,所以保证了数据是最新的
    最后执行：http://localhost:8088/getUsers?nickname=smile 会执行缓存*/
    @RequestMapping("/getPutUsers")
    @CachePut(value="usersCache",key="#nickname")
    public List<User> getPutUsers(String nickname) {
        List<User> users=userRepository.findByNickname(nickname);
        System.out.println("执行了数据库操作");
        return users;
    }

//   allEntries 是 boolean 类型，表示是否需要清除缓存中的所有元素。默认为 false，表示不需要。当指定了 allEntries 为 true 时，Spring Cache 将忽略指定的 key。有的时候我们需要 Cache 一下清除所有的元素，这比一个一个清除元素更有效率。
    @RequestMapping("/allEntries")
    @CacheEvict(value="usersCache", allEntries=true)
    public String  allEntries(String nickname) {
        String msg="执行了allEntries";
        System.out.println(msg);
        return msg;
    }

//    作用是即使方法执行失败也会干掉所有的缓存，也就是在执行方法之前先执行缓存操作
    @RequestMapping("/beforeInvocation")
    @CacheEvict(value="usersCache", allEntries=true, beforeInvocation=true)
    public void beforeInvocation() {
        throw new RuntimeException("test beforeInvocation");
    }
}