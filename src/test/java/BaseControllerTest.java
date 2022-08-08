import com.to.reggie.ReggieApplication;
import com.to.reggie.common.R;
import com.to.reggie.controller.BaseController;
import com.to.reggie.entity.Employee;
import com.to.reggie.service.ex.UserNotFoundException;
import com.to.reggie.service.ex.UsernameDuplicatedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@Slf4j
@SpringBootTest(classes = ReggieApplication.class)
public class BaseControllerTest {

    @Test
    void hashMapTest() {
        Exception e = new UsernameDuplicatedException();
        HashMap<Object, R> hashMap = new HashMap<>();
        log.info(String.valueOf(e.getClass()));
        log.info(String.valueOf(UsernameDuplicatedException.class));
        hashMap.put(UsernameDuplicatedException.class, R.ex(4000,e));
        log.info(String.valueOf(hashMap.get(e.getClass())));
    }

    @Test
    void javaDataTypeTest() {
        //Long用==比较 小数可以，大数不可以==
        Long a = 1556543180247822337L, b= 1556543180247822337L;
        log.info(String.valueOf(a==b));
        Employee c = new Employee(), d = new Employee();
        c.setId(a);
        d.setId(b);
        log.info(String.valueOf(c.getId()==d.getId()));
    }
}
