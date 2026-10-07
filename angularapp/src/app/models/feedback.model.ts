import { Driver } from './driver.model';
import { User } from './user.model';

export class Feedback {
  feedbackId?: number;
  feedbackText: string = '';
  date: Date = new Date();
  userId: number = 0;
  driverId?: number; // Optional for non-driver feedback
  category: string = '';
  rating: number = 0;

  // AI generated attributes (populated automatically by the backend on creation)
  sentiment?: string; // "Positive" | "Neutral" | "Negative"
  sentimentScore?: number; // 0.0 (very negative) - 1.0 (very positive)
  aiTags?: string; // comma-separated theme tags

  // The backend sends / expects the related entities nested ({ user: {...}, driver: {...} }).
  user?: User;
  driver?: Driver;
}
