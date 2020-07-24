package QueryManager.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class QueryResult<K,V> implements Serializable {
    private static final long serialVersionUID = 84022250479993108L;

    private Map<K,V> result;

    public QueryResult(){
        result = new HashMap<>();
    }

    public void setResult(Map<K, V> result) {
        this.result = result;
    }

    public Map<K,V> getResult(){
        return result;
    }

    public void clear(){
        result.clear();
    }

    public int size(){
        return result.size();
    }
}
