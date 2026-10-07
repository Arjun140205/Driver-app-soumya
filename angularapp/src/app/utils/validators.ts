import { AbstractControl, ValidationErrors } from '@angular/forms';

/** Form-group validator: "confirmPassword" must equal "password". */
export function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirm = group.get('confirmPassword')?.value;
  return confirm && password !== confirm ? { mismatch: true } : null;
}
