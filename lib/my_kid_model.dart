class MyKidModel {
  String? name;
  String? icNo;
  String? birthCertNo;
  String? gender;
  String? citizenship;
  String? registrationDate;
  String? dob;
  String? pob;
  String? address1;
  String? address2;
  String? address3;
  String? postcode;
  String? city;
  String? state;
  Parent? father;
  Parent? mother;

  MyKidModel({
    this.name,
    this.icNo,
    this.birthCertNo,
    this.gender,
    this.citizenship,
    this.registrationDate,
    this.dob,
    this.pob,
    this.address1,
    this.address2,
    this.address3,
    this.postcode,
    this.city,
    this.state,
    this.father,
    this.mother,
  });

  // fromJson method for MyKidModel
  factory MyKidModel.fromJson(Map<String, dynamic> json) {
    Map<String, dynamic> mykidData = json['mykid'];
    return MyKidModel(
      name: mykidData['name'],
      icNo: mykidData['icno'],
      birthCertNo: mykidData['birth_cert_no'],
      gender: mykidData['gender'],
      citizenship: mykidData['citizenship'],
      registrationDate: mykidData['registration_date'],
      dob: mykidData['date_of_birth'],
      pob: mykidData['place_of_birth'],
      address1: mykidData['address1'],
      address2: mykidData['address2'],
      address3: mykidData['address3'],
      postcode: mykidData['poscode'],
      city: mykidData['city'],
      state: mykidData['state'],
      father: mykidData['father'] != null
          ? Parent.fromJson(mykidData['father'])
          : null,
      mother: mykidData['mother'] != null
          ? Parent.fromJson(mykidData['mother'])
          : null,
    );
  }

  // toJson method for MyKidModel
  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'icno': icNo,
      'birth_cert_no': birthCertNo,
      'gender': gender,
      'citizenship': citizenship,
      'registration_date': registrationDate,
      'date_of_birth': dob,
      'place_of_birth': pob,
      'address1': address1,
      'address2': address2,
      'address3': address3,
      'poscode': postcode,
      'city': city,
      'state': state,
      'father': father?.toJson(),
      'mother': mother?.toJson(),
    };
  }

  // copyWith method for MyKidModel
  MyKidModel copyWith({
    String? name,
    String? icNo,
    String? birthCertNo,
    String? gender,
    String? citizenship,
    String? registrationDate,
    String? dob,
    String? pob,
    String? address1,
    String? address2,
    String? address3,
    String? postcode,
    String? city,
    String? state,
    Parent? father,
    Parent? mother,
  }) {
    return MyKidModel(
      name: name ?? this.name,
      icNo: icNo ?? this.icNo,
      birthCertNo: birthCertNo ?? this.birthCertNo,
      gender: gender ?? this.gender,
      citizenship: citizenship ?? this.citizenship,
      registrationDate: registrationDate ?? this.registrationDate,
      dob: dob ?? this.dob,
      pob: pob ?? this.pob,
      address1: address1 ?? this.address1,
      address2: address2 ?? this.address2,
      address3: address3 ?? this.address3,
      postcode: postcode ?? this.postcode,
      city: city ?? this.city,
      state: state ?? this.state,
      father: father ?? this.father,
      mother: mother ?? this.mother,
    );
  }
}

class Parent {
  String? name;
  String? icNo;
  String? race;
  String? religion;
  String? residentType;

  Parent({
    this.name,
    this.icNo,
    this.race,
    this.religion,
    this.residentType,
  });

  // fromJson method for Parent
  factory Parent.fromJson(Map<String, dynamic> json) {
    return Parent(
      name: json['name'],
      icNo: json['icno'],
      race: json['race'],
      religion: json['religion'],
      residentType: json['resident_type'],
    );
  }

  // toJson method for Parent
  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'icno': icNo,
      'race': race,
      'religion': religion,
      'resident_type': residentType,
    };
  }

  // copyWith method for Parent
  Parent copyWith({
    String? name,
    String? icNo,
    String? race,
    String? religion,
    String? residentType,
  }) {
    return Parent(
      name: name ?? this.name,
      icNo: icNo ?? this.icNo,
      race: race ?? this.race,
      religion: religion ?? this.religion,
      residentType: residentType ?? this.residentType,
    );
  }
}
