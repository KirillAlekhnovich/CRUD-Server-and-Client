package cz.cvut.fit.alekhkir.tjv.client.ui;

import cz.cvut.fit.alekhkir.tjv.client.data.PlayerClient;
import cz.cvut.fit.alekhkir.tjv.client.data.SponsorClient;
import cz.cvut.fit.alekhkir.tjv.client.data.TeamClient;
import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class ClientPromptProvider implements PromptProvider {
    private final PlayerClient playerClient;
    private final SponsorClient sponsorClient;
    private final TeamClient teamClient;

    public ClientPromptProvider(PlayerClient playerClient, SponsorClient sponsorClient, TeamClient teamClient) {
        this.playerClient = playerClient;
        this.sponsorClient = sponsorClient;
        this.teamClient = teamClient;
    }

    /* Prints prompt correctly if we're editing several entities */
    public String generateOutputString(){
        boolean playerChosen = false, sponsorChosen = false, teamChosen = false;
        String output = "client";
        if (playerClient.getCurrentPlayer() != null){
            playerChosen = true;
        }
        if (sponsorClient.getCurrentSponsor() != null){
            sponsorChosen = true;
        }
        if (teamClient.getCurrentTeam() != null){
            teamChosen = true;
        }
        if (playerChosen || sponsorChosen || teamChosen){
            output += "(";
        }
        if (playerChosen){
            output += "player=" + playerClient.getCurrentPlayer();
            if (sponsorChosen || teamChosen){
                output += ",";
            }
        }
        if (sponsorChosen){
            output += "sponsor=" + sponsorClient.getCurrentSponsor();
            if (teamChosen){
                output += ",";
            }
        }
        if (teamChosen){
            output += "team=" + teamClient.getCurrentTeam();
        }
        if (playerChosen || sponsorChosen || teamChosen){
            output += ")";
        }
        output += ":>";
        return output;
    }

    @Override
    public AttributedString getPrompt() {
        return new AttributedString(generateOutputString());
    }
}
