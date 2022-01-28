/******************************************************************************
 *
 * Copyright 2020-, UT-Battelle, LLC. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *******************************************************************************/
package io.bssw.psip.ui.views;

import com.vaadin.flow.server.StreamResource;
import org.vaadin.olli.FileDownloadWrapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.bssw.psip.backend.data.Activity;
import io.bssw.psip.ui.layout.size.Vertical;
import io.bssw.psip.ui.util.UIUtils;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;

import io.bssw.psip.backend.service.ActivityService;
import io.bssw.psip.ui.MainLayout;
import io.bssw.psip.ui.components.FlexBoxLayout;
import io.bssw.psip.ui.layout.size.Horizontal;
import io.bssw.psip.ui.layout.size.Uniform;

import java.awt.*;
import java.io.ByteArrayInputStream;

@SuppressWarnings("serial")
@PageTitle("Tracking")
@Route(value = "tracking", layout = MainLayout.class)
public class Tracking extends ViewFrame  implements HasUrlParameter<String> {
	private ActivityService activityService;
	private VerticalLayout mainLayout;
	private Html description;

	public Tracking(@Autowired ActivityService activityService) {
		this.activityService = activityService;
		setViewContent(createContent());
	}

	private Component createContent() {
		description = new Html("<p></p>");
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(false);
		mainLayout.setHeightFull();
		FlexBoxLayout content = new FlexBoxLayout(mainLayout);
		content.setFlexDirection(FlexDirection.COLUMN);
		content.setMargin(Horizontal.AUTO);
		content.setMaxWidth("840px");
		content.setPadding(Uniform.RESPONSIVE_L);
		content.setHeightFull();
		return content;
	}

	private static void AddPTCGrade(){

	}

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String desc = "";
		if (parameter.isEmpty()) {
			mainLayout.removeAll();

			Activity activity = activityService.getActivity("Tracking");
			desc = activity.getDescription();

			Button button1 = new Button("Take Assessment", btn1event -> MainLayout.navigate(Assessment.class, ""));
			button1.setWidthFull();
			button1.setHeight("50%");
			Button button2 = new Button("Load Assessment Results or Previous Tracking Cards", btn2event -> MainLayout.navigate(Tracking.class, "suggestions"));
			button2.setWidthFull();
			button2.setHeight("50%");

			VerticalLayout vrt = new VerticalLayout(button1, button2);
			vrt.setHeight("600px");

			mainLayout.addAndExpand(vrt);
		} else if(parameter.equals("suggestions")){
			mainLayout.removeAll();

			Label head = new Label("Suggested Goals");
			head.setWidth(null);
			HorizontalLayout top = new HorizontalLayout(head);
			top.setWidth("100%");
			top.setAlignItems(FlexComponent.Alignment.CENTER);

			HorizontalLayout[] suggestions = new HorizontalLayout[4];
			for(int i = 0; i < 4; ++i){
				Button example = new Button("Example Suggestion");
				example.setWidth("50%");
				Button example1 = new Button("Example Suggestion");
				example1.setWidth("50%");
				suggestions[i] = new HorizontalLayout(example, example1);
				suggestions[i].setWidth("100%");
			}

			Button customGoal = new Button("Custom Goal", btnCustomEvent -> MainLayout.navigate(Tracking.class, "PTCCreator"));
			Button saveResults = new Button("Save Results");
			HorizontalLayout other = new HorizontalLayout(customGoal, saveResults);
			other.setWidth("100%");
			other.setAlignItems(FlexComponent.Alignment.STRETCH);

			VerticalLayout vrt = new VerticalLayout(top, suggestions[0], suggestions[1], suggestions[2], suggestions[3], other);
			vrt.setWidth("100%");
			mainLayout.addAndExpand(vrt);
		} else if(parameter.equals("PTCCreator")){
			mainLayout.removeAll();

			VerticalLayout ptcSketch = new VerticalLayout();

			TextArea userStory = new TextArea("User Story");
			userStory.setWidth("80%");
			Button save = new Button("Save Results");
			save.setWidth("20%");
			save.setHeight("100%");
			HorizontalLayout hz1 = new HorizontalLayout(userStory, save);
			hz1.setWidthFull();

			Button addGrade = new Button("+");
			addGrade.setWidth("80%");
			addGrade.getElement().addEventListener("click", e -> ptcSketch.add(new Button("Example")));
			HorizontalLayout hz2 = new HorizontalLayout(addGrade);
			hz2.setWidthFull();

			Button back = new Button("Back", backBtn -> MainLayout.navigate(Tracking.class, ""));
			Button continueBtn = new Button("Continue", cntBtn -> MainLayout.navigate(Tracking.class, "Preview"));
			HorizontalLayout hz3 = new HorizontalLayout(back, continueBtn);
			hz3.setWidthFull();

			VerticalLayout vrt = new VerticalLayout(hz1, hz2, ptcSketch, hz3);
			vrt.setWidth("100%");
			mainLayout.addAndExpand(vrt);
		} else if(parameter.equals("Preview")) {
			mainLayout.removeAll();

			Label label = new Label("Progress Tracking Card");
			HorizontalLayout top = new HorizontalLayout(label);
			top.setWidthFull();

			Label placeholder = new Label("This is a placeholder for the progress tracking card");
			HorizontalLayout mid = new HorizontalLayout(placeholder);
			mid.setWidthFull();

			Button back = new Button("Back", backBtn -> MainLayout.navigate(Tracking.class, "PTCCreator"));
			Button saveCard = new Button("Save PTC");
			FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
					new StreamResource("foo.txt", () -> new ByteArrayInputStream("foo".getBytes())));
			buttonWrapper.wrapComponent(saveCard);
			saveCard.addClickListener(e -> buttonWrapper.wrapComponent(saveCard));
			HorizontalLayout bot = new HorizontalLayout(back, buttonWrapper);
			bot.setWidthFull();


			VerticalLayout vrt = new VerticalLayout(top, mid, bot);
			vrt.setWidth("100%");
			mainLayout.addAndExpand(vrt);
		}
		description.getElement().setText(desc);
	}
}
