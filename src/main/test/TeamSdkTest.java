import app.Application;
import app.po.Project;
import app.repository.ProjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TeamSdkTest {
    @Autowired
    ProjectRepository projectRepository;
    @Test
    public void testT(){
        System.out.println("xx");
        Project s = new Project();
        s.setSourceId("xx");

        s.setName("yy");
        projectRepository.save(s);
    }
}
