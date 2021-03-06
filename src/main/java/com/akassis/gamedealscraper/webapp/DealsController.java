package com.akassis.gamedealscraper.webapp;

import com.akassis.gamedealscraper.domain.Deal;
import com.akassis.gamedealscraper.scraper.Scraper;
import com.akassis.gamedealscraper.utils.Convert;
import com.akassis.gamedealscraper.utils.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class DealsController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String deals(Model model,
                        @RequestParam(value="f", defaultValue="1") String free,
                        @RequestParam(value="r", defaultValue="0") String reddit,
                        @RequestParam(value="h", defaultValue="0") String humble) {

        List<Deal> deals = new ArrayList<>();

        boolean freeOnly = Convert.stringToBoolean(free);
        boolean scrapeReddit = Convert.stringToBoolean(reddit);
        boolean scrapeHumble = Convert.stringToBoolean(humble);

        if (scrapeHumble) {
            List<Deal> humbleDeals = Scraper.scrapeHumbleStore();
            deals.addAll(humbleDeals);
        }

        // Always scrape reddit last to avoid showing duplicate results
        if (scrapeReddit) {
            List<Deal> redditDeals = Scraper.scrapeSubreddit("GameDeals");
            // If we find a reddit post for a deal we've already discovered, discard it
            for (Iterator<Deal> it = redditDeals.iterator(); it.hasNext();) {
                Deal redditDeal = it.next();
                for (Deal deal : deals) {
                    if (deal.getTitle().equals(redditDeal.getTitle())) { // TODO: Replace the title check here with some kind of unique ID once database is implemented
                        it.remove();
                    }
                }
            }

            deals.addAll(redditDeals);
        }

        if (freeOnly) {
            deals = Scraper.getFreeResults(deals);
        }

        model.addAttribute("deals", deals);
        model.addAttribute("iconMap", Deal.getIconMap());
        model.addAttribute("version", Logger.getVersion());
        model.addAttribute("pageTitle", Logger.getName());

        return "base";
    }
}
