import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HumbleResponse {
    @JsonProperty("results")
    private List<HumbleDeal> results;

    public HumbleDeal get(int index){
        return this.results.get(index);
    }

    public HumbleDeal get(String title) {
        title = title.toLowerCase().trim();
        for (HumbleDeal d : results) {
            String currentTitle = d.human_name.toLowerCase().trim();
            if (title.equals(currentTitle)) {
                return d;
            }
        }
        return null;
    }

    public List<HumbleDeal> getResults() {
        return results;
    }
}