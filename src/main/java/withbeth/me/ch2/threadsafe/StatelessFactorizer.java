package withbeth.me.ch2.threadsafe;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
public class StatelessFactorizer extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer i = extractFromRequest(req);

        List<Integer> factors = factor(i);

        encodeIntoResponse(resp, factors);
    }

    private void encodeIntoResponse(HttpServletResponse resp, List<Integer> factors) throws IOException {
        resp.setStatus(HttpStatus.OK.value());
        resp.setContentType(MediaType.TEXT_PLAIN_VALUE);
        resp.getWriter().println(factors);
    }

    public static List<Integer> factor(int n) {
        List<Integer> factors = new ArrayList<>();
        int divisor = 2;
        while (n > 1) {
            while (n % divisor == 0) {
                factors.add(divisor);
                n /= divisor;
            }
            divisor++;
            if (divisor * divisor > n) {
                if (n > 1) {
                    factors.add(n);
                }
                break;
            }
        }
        return factors;
    }


    private Integer extractFromRequest(HttpServletRequest req) {
        return Integer.parseInt(req.getParameter("n"));
    }
}
