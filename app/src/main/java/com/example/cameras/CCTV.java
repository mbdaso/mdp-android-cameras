package com.example.cameras;

public class CCTV {
    KML kml;
}

class KML {
    DOCUMENT Document;
}

class DOCUMENT {
    String name;
    // other fields
    PLACEMARK[] Placemark;
}

class PLACEMARK {
    String description;
    EXTENDEDDATA ExtendedData;
}

class EXTENDEDDATA {
    DATA[] Data;
}

class DATA {
    String Value;
    String _name;
}