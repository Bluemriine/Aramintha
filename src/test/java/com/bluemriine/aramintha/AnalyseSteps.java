package com.bluemriine.aramintha;

import com.bluemriine.aramintha.data.DataHolder;
import com.bluemriine.aramintha.data.ResultatAtkDefMembreDto;
import com.bluemriine.aramintha.data.ResultatContributionMembreDto;
import com.bluemriine.aramintha.service.CaptureService;
import com.bluemriine.aramintha.service.orc.AtkDefORCService;
import com.bluemriine.aramintha.service.orc.ContributionORCService;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

/** Classe de test de la feature  Extraction ATK/DEF */
public class AnalyseSteps {

	/** Optional contenant le fichier à analyser */
	private Optional<File> optScreenshot;

	@Given("Image de départ {string}")
	public void image_de_depart(String string) {
		DataHolder.getInstance().getListResultatAtkDefMembre().clear();
		DataHolder.getInstance().setAnalyseBouton(new JButton());
		Path relPath = Paths.get("src", "test", "resources", string);
		String absPath = relPath.toFile().getAbsolutePath();
		optScreenshot = Optional.of(new File(absPath));
	}

	@When("Je passe le fichier à la reconnaissance de texte")
	public void je_passe_le_fichier_a_la_reconnaissance_de_texte() {
		CaptureService service = new CaptureService();
		service.analyse(new AtkDefORCService(), optScreenshot);
	}

	@Then("J ai les bons scores pour l image {string}")
	public void j_ai_les_bons_scores_pour_l_image(String string) {
		Set<ResultatAtkDefMembreDto> listResultatAtkDefMembreExtracted = DataHolder.getInstance().getListResultatAtkDefMembre();
		ResultatAtkDefMembreDto[] results = new ResultatAtkDefMembreDto[listResultatAtkDefMembreExtracted.size()];
		listResultatAtkDefMembreExtracted.toArray(results);

		ResultatAtkDefMembreDto mb1 = new ResultatAtkDefMembreDto();
		ResultatAtkDefMembreDto mb2 = new ResultatAtkDefMembreDto();
		ResultatAtkDefMembreDto mb3 = new ResultatAtkDefMembreDto();

		if ("testA.png".equalsIgnoreCase(string)) {
			mb1 = ResultatAtkDefMembreDto.builder().pseudo("Bluemriine").defWin(8).defDraw(9).defLoose(12).atkWin(28).atkDraw(9).atkLoose(5).build();
			mb2 = ResultatAtkDefMembreDto.builder().pseudo("Lustqq").defWin(13).defDraw(12).defLoose(16).atkWin(28).atkDraw(9).atkLoose(5).build();
			mb3 = ResultatAtkDefMembreDto.builder().pseudo("Aoshane").defWin(11).defDraw(11).defLoose(6).atkWin(24).atkDraw(16).atkLoose(1).build();
		}
		else if ("testB.png".equalsIgnoreCase(string)) {
			mb1 = ResultatAtkDefMembreDto.builder().pseudo("LuHao").defWin(3).defDraw(5).defLoose(3).atkWin(34).atkDraw(6).atkLoose(2).build();
			mb2 = ResultatAtkDefMembreDto.builder().pseudo("Meili").defWin(4).defDraw(9).defLoose(12).atkWin(28).atkDraw(12).atkLoose(2).build();
			mb3 = ResultatAtkDefMembreDto.builder().pseudo("Voci").defWin(2).defDraw(2).defLoose(3).atkWin(25).atkDraw(12).atkLoose(4).build();
		}
		else if ("testC.png".equalsIgnoreCase(string)) {
			mb1 = ResultatAtkDefMembreDto.builder().pseudo("BolDeRyry").defWin(10).defDraw(13).defLoose(16).atkWin(18).atkDraw(9).atkLoose(12).build();
			mb2 = ResultatAtkDefMembreDto.builder().pseudo("HenryTurker").defWin(3).defDraw(8).defLoose(21).atkWin(22).atkDraw(17).atkLoose(3).build();
			mb3 = ResultatAtkDefMembreDto.builder().pseudo("Leodaguan").defWin(12).defDraw(13).defLoose(9).atkWin(27).atkDraw(14).atkLoose(1).build();
		}

		Assert.assertEquals(results[0].getPseudo().split(" ")[0], mb1.getPseudo());
		Assert.assertEquals(results[0].getAtkDraw(), mb1.getAtkDraw());
		Assert.assertEquals(results[0].getAtkLoose(), mb1.getAtkLoose());
		Assert.assertEquals(results[0].getAtkWin(), mb1.getAtkWin());
		Assert.assertEquals(results[0].getDefDraw(), mb1.getDefDraw());
		Assert.assertEquals(results[0].getDefLoose(), mb1.getDefLoose());
		Assert.assertEquals(results[0].getDefWin(), mb1.getDefWin());

		Assert.assertEquals(results[1].getPseudo().split(" ")[0], mb2.getPseudo());
		Assert.assertEquals(results[1].getAtkDraw(), mb2.getAtkDraw());
		Assert.assertEquals(results[1].getAtkLoose(), mb2.getAtkLoose());
		Assert.assertEquals(results[1].getAtkWin(), mb2.getAtkWin());
		Assert.assertEquals(results[1].getDefDraw(), mb2.getDefDraw());
		Assert.assertEquals(results[1].getDefLoose(), mb2.getDefLoose());
		Assert.assertEquals(results[1].getDefWin(), mb2.getDefWin());

		Assert.assertEquals(results[2].getPseudo().split(" ")[0], mb3.getPseudo());
		Assert.assertEquals(results[2].getAtkDraw(), mb3.getAtkDraw());
		Assert.assertEquals(results[2].getAtkLoose(), mb3.getAtkLoose());
		Assert.assertEquals(results[2].getAtkWin(), mb3.getAtkWin());
		Assert.assertEquals(results[2].getDefDraw(), mb3.getDefDraw());
		Assert.assertEquals(results[2].getDefLoose(), mb3.getDefLoose());
		Assert.assertEquals(results[2].getDefWin(), mb3.getDefWin());

		Assert.assertEquals(3, listResultatAtkDefMembreExtracted.size());
	}

	@When("Je passe le fichier à la reconnaissance de texte de contribution")
	public void je_passe_le_fichier_a_la_reconnaissance_de_texte_de_contribution() {
		CaptureService service = new CaptureService();
		service.analyse(new ContributionORCService(), optScreenshot);
	}

	@Then("J ai les bons scores de contribution pour l image")
	public void j_ai_les_bons_scores_de_contribution_pour_l_image() {
		Set<ResultatContributionMembreDto> listResultatMembreExtracted = DataHolder.getInstance().getListResultatContributionMembre();
		ResultatContributionMembreDto[] results = new ResultatContributionMembreDto[listResultatMembreExtracted.size()];
		listResultatMembreExtracted.toArray(results);

		ResultatContributionMembreDto mb1 = ResultatContributionMembreDto.builder().pseudo("Voci").points(48).build();
		ResultatContributionMembreDto mb2 = ResultatContributionMembreDto.builder().pseudo("MorriganBlan").points(44).build();
		ResultatContributionMembreDto mb3 = ResultatContributionMembreDto.builder().pseudo("Aoshane").points(44).build();
		ResultatContributionMembreDto mb4 = ResultatContributionMembreDto.builder().pseudo("Lizzee").points(44).build();

		Assert.assertEquals(results[0].getPseudo().split(" ")[0], mb1.getPseudo());
		Assert.assertEquals(results[0].getPoints(), mb1.getPoints());
		Assert.assertEquals(results[1].getPseudo().split(" ")[0], mb2.getPseudo());
		Assert.assertEquals(results[1].getPoints(), mb2.getPoints());
		Assert.assertEquals(results[2].getPseudo().split(" ")[0], mb3.getPseudo());
		Assert.assertEquals(results[2].getPoints(), mb3.getPoints());
		Assert.assertEquals(results[3].getPseudo().split(" ")[0], mb4.getPseudo());
		Assert.assertEquals(results[3].getPoints(), mb4.getPoints());

		Assert.assertEquals(listResultatMembreExtracted.size(), 4);
	}

}