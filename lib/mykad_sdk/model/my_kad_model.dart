class MyKadModel {
  String? name;
  String? gmpcName;
  String? kptName;
  String? icNo;
  String? oldIcNo;
  String? dob;
  String? pob;
  String? gender;
  String? citizenship;
  String? issueDate;
  String? race;
  String? religion;
  String? address1;
  String? address2;
  String? address3;
  String? postCode;
  String? city;
  String? state;
  String? photo;

  MyKadModel({
    this.name,
    this.gmpcName,
    this.kptName,
    this.icNo,
    this.oldIcNo,
    this.dob,
    this.pob,
    this.gender,
    this.citizenship,
    this.issueDate,
    this.race,
    this.religion,
    this.address1,
    this.address2,
    this.address3,
    this.postCode,
    this.city,
    this.state,
    this.photo,
  });

  // fromJson method
  factory MyKadModel.fromJson(Map<String, dynamic> json) {
    Map<String, dynamic> mykadData = json['mykad'];
    return MyKadModel(
      name: mykadData['name'],
      gmpcName: mykadData['gmpc_name'],
      kptName: mykadData['kpt_name'],
      icNo: mykadData['icno'],
      oldIcNo: mykadData['old_icno'],
      dob: mykadData['date_of_birth'],
      pob: mykadData['place_of_birth'],
      gender: mykadData['gender'],
      citizenship: mykadData['citizenship'],
      issueDate: mykadData['issue_date'],
      race: mykadData['race'],
      religion: mykadData['religion'],
      address1: mykadData['address1'],
      address2: mykadData['address2'],
      address3: mykadData['address3'],
      postCode: mykadData['poscode'],
      city: mykadData['city'],
      state: mykadData['state'],
      photo: mykadData['photo'],
    );
  }

  // toJson method
  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'gmpc_name': gmpcName,
      'kpt_name': kptName,
      'icno': icNo,
      'old_icno': oldIcNo,
      'date_of_birth': dob,
      'place_of_birth': pob,
      'gender': gender,
      'citizenship': citizenship,
      'issue_date': issueDate,
      'race': race,
      'religion': religion,
      'address1': address1,
      'address2': address2,
      'address3': address3,
      'poscode': postCode,
      'city': city,
      'state': state,
      'photo': photo,
    };
  }

  // copyWith method
  MyKadModel copyWith({
    String? name,
    String? gmpcName,
    String? kptName,
    String? icNo,
    String? oldIcNo,
    String? dob,
    String? pob,
    String? gender,
    String? citizenship,
    String? issueDate,
    String? race,
    String? religion,
    String? address1,
    String? address2,
    String? address3,
    String? postCode,
    String? city,
    String? state,
    String? photo,
  }) {
    return MyKadModel(
      name: name ?? this.name,
      gmpcName: gmpcName ?? this.gmpcName,
      kptName: kptName ?? this.kptName,
      icNo: icNo ?? this.icNo,
      oldIcNo: oldIcNo ?? this.oldIcNo,
      dob: dob ?? this.dob,
      pob: pob ?? this.pob,
      gender: gender ?? this.gender,
      citizenship: citizenship ?? this.citizenship,
      issueDate: issueDate ?? this.issueDate,
      race: race ?? this.race,
      religion: religion ?? this.religion,
      address1: address1 ?? this.address1,
      address2: address2 ?? this.address2,
      address3: address3 ?? this.address3,
      postCode: postCode ?? this.postCode,
      city: city ?? this.city,
      state: state ?? this.state,
      photo: photo ?? this.photo,
    );
  }
}
