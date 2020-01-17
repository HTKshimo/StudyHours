//
//  LoginViewController.swift
//  StudyHours
//
//  Created by Shuang Li on 1/16/20.
//  Copyright © 2020 Shuang Li. All rights reserved.
//

import UIKit
import FirebaseAuth

class LoginViewController: UIViewController {

    @IBOutlet weak var emailField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    @IBAction func SignIn(_ sender: Any) {
        guard let email = self.emailField.text, let password = self.passwordField.text else {
//          self.showMessagePrompt("email/password can't be empty")
          return
        }
//        showSpinner {
          // [START headless_email_auth]
          Auth.auth().signIn(withEmail: email, password: password) { [weak self] authResult, error in
            guard let strongSelf = self else { return }
            // [START_EXCLUDE]
//            strongSelf.hideSpinner {
              if let error = error {
                print("Wrong password")
//                strongSelf.showMessagePrompt(error.localizedDescription)
//                return
              }else{
                print("strongSelf.navigationController?.popViewController(animated: true)")
                //Expect: navigate to the first scene of navigationView
                //Actual: no action
                strongSelf.navigationController?.popViewController(animated: true)
            }
//              strongSelf.navigationController?.popViewController(animated: true)
//            let storyboard : UIStoryboard = UIStoryboard(name: "AccountStoryboard", bundle: nil)
//            let vc : RecordViewController = storyboard.instantiateViewController(withIdentifier: "record") as! RecordViewController
//
//            let navigationController = UINavigationController(rootViewController: vc)
//
//            self?.present(navigationController, animated: true, completion: nil)
            }
            // [END_EXCLUDE]
//          }
          // [END headless_email_auth]
        }
//    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
