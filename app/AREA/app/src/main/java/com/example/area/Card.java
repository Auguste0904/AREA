package com.example.area;

import java.util.ArrayList;

public class Card {
    private String _TriggerService;
    private String _Conditions;
    private String _ActionService;
    private String _Actions;
    private String _token;
    private String _id;

    public Card(String TriggerService, String Conditions, String ActionServices, String Actions, String id, String token) {
        _TriggerService = TriggerService;
        _Conditions = Conditions;
        _ActionService = ActionServices;
        _Actions = Actions;
        _token = token;
        _id = id;
    }

    public String get_TriggerService() {
        return _TriggerService;
    }

    public String get_Conditions() {
        return _Conditions;
    }

    public String get_ActionService() {
        return _ActionService;
    }

    public String get_Actions() {
        return _Actions;
    }

    public String get_token() {
        return _token;
    }

    public String get_id() {
        return _id;
    }

    // Ajouter le handling des boutons Edit et Delete (Peut-être que faut le faire dans CardAdaptater.java)
}