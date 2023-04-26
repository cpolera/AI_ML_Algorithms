package com.controllers.viewers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping("/view-qap")
    public String viewProducts() {
        return "view-qap";
    }

    @RequestMapping("/add-qap") // TODO: remove this separate page and make single QAP page
    public String addProducts() {
        return "add-qap";
    }

    @RequestMapping("/bayesian")
    public String viewBayesianClassifier() {
        return "bayesian";
    }

    @RequestMapping("/feedforward")
    public String viewFeedforward() {
        return "feedforward";
    }

    @RequestMapping("/markov")
    public String viewMarkovModel() {
        return "markov";
    }

    @RequestMapping("/nearest-neighbor")
    public String viewNearestNeighbor() {
        return "nearest-neighbor";
    }
}
