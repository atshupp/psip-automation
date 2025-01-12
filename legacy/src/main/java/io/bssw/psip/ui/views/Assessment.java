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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.olli.ClipboardHelper;

import com.github.appreciated.apexcharts.ApexCharts;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.WildcardParameter;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;

import io.bssw.psip.backend.data.Activity;
import io.bssw.psip.backend.data.Category;
import io.bssw.psip.backend.data.Item;
import io.bssw.psip.backend.service.ActivityService;
import io.bssw.psip.ui.MainLayout;
import io.bssw.psip.ui.components.FlexBoxLayout;
import io.bssw.psip.ui.components.RadarChart;
import io.bssw.psip.ui.components.ScoreItem;
import io.bssw.psip.ui.components.ScoreSlider;
import io.bssw.psip.ui.layout.size.Horizontal;
import io.bssw.psip.ui.layout.size.Uniform;
import io.bssw.psip.ui.util.Strong;
import io.bssw.psip.ui.util.UIUtils;

@SuppressWarnings("serial")
@PageTitle("Assessment")
@Route(value = "assessment", layout = MainLayout.class)
public class Assessment extends ViewFrame implements HasUrlParameter<String> {
	private Label description;
	private VerticalLayout mainLayout;
	private ActivityService activityService;
	
	public Assessment(@Autowired ActivityService activityService) {
		this.activityService = activityService;
		setViewContent(createContent());
	}

	private Component createContent() {
		description = new Label();
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(false);
		mainLayout.setHeightFull();
		FlexBoxLayout content = new FlexBoxLayout(new VerticalLayout(description), mainLayout);
		content.setFlexDirection(FlexDirection.COLUMN);
		content.setMargin(Horizontal.AUTO);
		content.setMaxWidth("840px");
		content.setPadding(Uniform.RESPONSIVE_L);
		content.setHeightFull();
		return content;
	}
	
	private void createItemLayout(Item item) {
		mainLayout.removeAll();
		ScoreItem scoreItem = new ScoreItem(item);
		String path = item.getCategory().getActivity().getPath() + "/" + item.getCategory().getPath() + "/" + item.getPath();
		Item prevItem =  activityService.getPrevItem(path);
		Button button1 = UIUtils.createLargeButton(VaadinIcon.CHEVRON_CIRCLE_LEFT);
		button1.getElement().getStyle().set("background", "#F3F5F7").set("font-size", "30px"); // FIXME: Don't hard code background
		button1.getElement().addEventListener("click", e -> {
			MainLayout.navigate(Assessment.class, prevItem.getCategory().getPath() + "/" + prevItem.getPath());
		});
		if (prevItem == null) {
			button1.getElement().setEnabled(false);
		}
		Item nextItem = activityService.getNextItem(path);
		Button button2 =  UIUtils.createLargeButton(VaadinIcon.CHEVRON_CIRCLE_RIGHT);
		button2.getElement().getStyle().set("background", "#F3F5F7").set("font-size", "30px"); // FIXME: Don't hard code background
		button2.getElement().addEventListener("click", e -> {
			MainLayout.navigate(Assessment.class, nextItem.getCategory().getPath() + "/" + nextItem.getPath());
		});
		if (nextItem == null) {
			button2.getElement().setEnabled(false);
		}
		HorizontalLayout hz = new HorizontalLayout(button1, button2);
		hz.setJustifyContentMode(JustifyContentMode.CENTER);
		hz.setWidthFull();
		Anchor anchor = new Anchor();
		anchor.setText("Show me my assessment");
		anchor.getElement().addEventListener("click", e -> MainLayout.navigate(Assessment.class, null) );
		HorizontalLayout hz2 = new HorizontalLayout(anchor);
		hz2.setJustifyContentMode(JustifyContentMode.CENTER);
		hz2.setWidthFull();
		VerticalLayout footer = new VerticalLayout(hz, hz2);
		footer.setMargin(false);
		mainLayout.addAndExpand(scoreItem, footer);
	}
	
	private void createCategoryLayout(Category category) {
		mainLayout.removeAll();
		FormLayout form = new FormLayout();
		form.getElement().getStyle().set("padding", "30px");
		for (Item item : category.getItems()) {
			ScoreSlider slider = new ScoreSlider(item);
			FormItem formItem = form.addFormItem(slider, item.getName());
			formItem.getElement().getStyle().set("--vaadin-form-item-label-width", "15em"); // Set width of label otherwise it will wrap
			formItem.getElement().getStyle().set("align-self", "flex-start");
			formItem.getElement().getStyle().set("padding", "5px");
			form.setColspan(formItem, 2); // FormLayout defaults to 2 columns so span both
		}
		
		Div div = new Div();
		div.add(new Paragraph(new Emphasis("The rows below show how well your team is doing for each practice. "
				+ "As your practices improve, you can always return to this page to update them directly.")));

		Anchor anchor = new Anchor();
		anchor.setText("Click here to assess your individual practices.");
		anchor.getElement().addEventListener("click", e -> MainLayout.navigate(Assessment.class, category.getPath() + "/" + category.getItems().get(0).getPath()));

		mainLayout.add(div, anchor, form);
	}
	
	private void createActivityLayout(Activity activity) {
		mainLayout.removeAll();

		Anchor saveAnchor = new Anchor();
		saveAnchor.setText("save your current assessment.");
		saveAnchor.getElement().addEventListener("click", e -> {
		    String url = generateSaveUrl(activity);
		    displaySaveDialog(url);
		});

		Div div = new Div();
		div.add(new Paragraph(new Emphasis("The diagram below shows how your project is progressing in all practice areas. "
				+ "You can come back to this page any time during the assessment to see your progress. ")));
		
		div.add(new Paragraph(new Emphasis(new Strong("We do not save your data in any way. If you refresh or close your browser, "
				+ "your assessment will be lost. We suggest you regularly use this link to "),
				saveAnchor)));
		
		Anchor startAnchor = new Anchor();
		startAnchor.setText("Click here to start assessing your practices.");
		startAnchor.getElement().addEventListener("click", e -> MainLayout.navigate(Assessment.class, activity.getCategories().get(0).getPath()));

		ApexCharts summary = createActivitySummary(activity);
		AtomicBoolean hasInput = new AtomicBoolean(false);
		activity.getCategories().forEach(c -> {
					if (!c.getItems().isEmpty()) {
						for (Item item : c.getItems()) {
							if (item.getScore().isPresent()) {
								int score = item.getScore().get();
								if(score > 0) {
									hasInput.set(true);
									break;
								}
							}
						}
					}
				});
		if(hasInput.get()) {
			Button btn1 = new Button("Suggest a Goal", click -> MainLayout.navigate(Tracking.class, "suggestions"));
			Button btn2 = new Button("Custom Goal", click -> MainLayout.navigate(Tracking.class, "PTCCreator"));
			btn1.setHeightFull();
			btn1.setWidth("30%");
			btn2.setHeightFull();
			btn2.setWidth("30%");
			HorizontalLayout hz = new HorizontalLayout(btn1, btn2);
			hz.setWidthFull();
			hz.setAlignItems(Alignment.CENTER);

			mainLayout.add(div, startAnchor, summary, hz);
		} else {
			mainLayout.add(div, startAnchor, summary);
		}
		mainLayout.setHorizontalComponentAlignment(Alignment.CENTER, startAnchor);
	}
	
	/**
	 * Generate a url that can be used to resume assessment
	 * @param activity
	 * @return url
	 */
	private String generateSaveUrl(Activity activity) {
		StringBuilder query = new StringBuilder();
		Iterator<Category> categoryIter = activity.getCategories().iterator();
		while (categoryIter.hasNext()) {
			Category category = categoryIter.next();
			query.append(category.getPath() + "=");
			StringBuilder nested = new StringBuilder();
			Iterator<Item> itemIter = category.getItems().iterator();
			while (itemIter.hasNext()) {
				Item item = itemIter.next();
				nested.append(item.getPath() + ":" + item.getScore().orElse(0));
				if (itemIter.hasNext()) {
					nested.append("+");
				}
			}
			query.append(nested.toString());
			if (categoryIter.hasNext()) {
				query.append("&");
			}
		}
		return getLocation() + RouteConfiguration.forSessionScope().getUrl(Assessment.class, "?" + query.toString());
	}
	
	/**
	 * Restore the state from the given query string
	 * 
	 * @param activity
	 * @param query query from URL
	 */
	private void restoreSaveUrl(Activity activity, Map<String, List<String>> parameters) {
		for (String categoryPath : parameters.keySet()) {
			List<String> values = parameters.get(categoryPath);
			if (!values.isEmpty()) {
				// Only use the first value
				for (String itemScore : values.get(0).split("\\+")) {
					String[] kv = itemScore.split(":");
					if (kv.length == 2) {
						Item item = activityService.getItem("assessment/" + categoryPath + "/" + kv[0]);
						if (item != null) {
							try {
								item.setScore(Integer.parseInt(kv[1]));
							} catch (NumberFormatException e) {
								// Skip it
							}
						}
					}
				}
			}
		}
	}
	
	private void displaySaveDialog(String url) {
	    Dialog dialog = new Dialog();
	    dialog.setCloseOnOutsideClick(false);
	    dialog.setWidth("500px");
	    Label header = new Label("Copy this URL and paste into your browser to resume your assessment.");
	    TextField field = new TextField();
	    field.setValue(url);
	    Button copyButton = new Button(VaadinIcon.COPY.create());
	    ClipboardHelper helper = new ClipboardHelper(url, copyButton);
	    HorizontalLayout fieldLayout = new HorizontalLayout(field, helper);
	    fieldLayout.setMargin(false);
	    fieldLayout.setWidthFull();
	    fieldLayout.setFlexGrow(1, field);
	    Anchor closeButton = new Anchor();
	    closeButton.setText("Close");
	    closeButton.getElement().addEventListener("click", event -> {
	        dialog.close();
	    });
	    VerticalLayout dialogLayout = new VerticalLayout(header, fieldLayout, closeButton);
	    dialogLayout.setHorizontalComponentAlignment(Alignment.START, header, field);
	    dialogLayout.setHorizontalComponentAlignment(Alignment.CENTER, closeButton);
	    dialog.add(dialogLayout);
	    dialog.open();
	}
	
	/**
	 * Get the location in the browser. NOTE this only works from the UI thread!
	 * @return location
	 */
	private static String getLocation(){
	    VaadinServletRequest request = (VaadinServletRequest) VaadinService.getCurrentRequest();
	    return request.getRequestURL().toString();
	}
	
	private ApexCharts createActivitySummary(Activity activity) {
		ApexCharts chart = new RadarChart(activity).build();
		chart.setWidth("100%");
		return chart;
	}
	
	@SuppressWarnings("unused")
	private Component createActivitySummaryAsProgress(Activity activity) {
		FormLayout form = new FormLayout();
		for (Category category : activity.getCategories()) {
			int score = 0;
			for (Item item : category.getItems()) {
				if (item.getScore().isPresent()) {
					score += item.getScore().get();
				}
			}
			ProgressBar bar = new ProgressBar(0, category.getItems().size() > 0 ? 100 * category.getItems().size() : 1);
			bar.getElement().getStyle().set("height", "10px");
			bar.setValue(score);
			if (category.getItems().isEmpty()) {
				bar.getElement().setEnabled(false);
			}
			FormItem formItem = form.addFormItem(bar, category.getName());
			formItem.getElement().getStyle().set("--vaadin-form-item-label-width", "15em"); // Set width of label otherwise it will wrap
			form.setColspan(formItem, 2); // FormLayout defaults to 2 columns so span both
		}
		return form;
	}

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String desc = "";
		if (parameter.isEmpty()) {
			Activity activity = activityService.getActivity("Assessment");
			desc = activity.getDescription();
			
			// Get query parameters and restore state if there are any
			Location location = event.getLocation();
		    QueryParameters queryParameters = location.getQueryParameters();
		    Map<String, List<String>> parametersMap = queryParameters.getParameters();
			if (!parametersMap.isEmpty()) {
				restoreSaveUrl(activity, parametersMap);
			}

			createActivityLayout(activity);
		} else if (parameter.contains("/")) {
			// Item
			Item item = activityService.getItem("assessment/" + parameter);
			if (item != null) {
				desc = item.getDescription();
				createItemLayout(item);
			} else {
				System.out.println("invalid parameter="+parameter);
			}
		} else {
			// Category
			Category category = activityService.getCategory("assessment/" + parameter);
			desc = category.getDescription();
			createCategoryLayout(category);
		}
		description.getElement().setText(desc);
	}

}
