#!/usr/bin/env python3
import sys
import os
import numpy as np
from PIL import Image
from scipy.ndimage import gaussian_filter

TARGET_PATH = "/Users/kishanpmevada/.gemini/antigravity/brain/6be36080-b75e-438c-b42a-222f9bf0f636/theme4_tactile_claymorphic_png_1794722436669.jpg"

def ssim_standard(img1, img2, sigma=1.5):
    """Standard Wang et al. (2004) Structural Similarity Index with Gaussian weighting."""
    C1 = (0.01) ** 2
    C2 = (0.03) ** 2

    mu1 = gaussian_filter(img1, sigma)
    mu2 = gaussian_filter(img2, sigma)

    mu1_sq = mu1 * mu1
    mu2_sq = mu2 * mu2
    mu1_mu2 = mu1 * mu2

    sigma1_sq = gaussian_filter(img1 * img1, sigma) - mu1_sq
    sigma2_sq = gaussian_filter(img2 * img2, sigma) - mu2_sq
    sigma12 = gaussian_filter(img1 * img2, sigma) - mu1_mu2

    num = (2 * mu1_mu2 + C1) * (2 * sigma12 + C2)
    den = (mu1_sq + mu2_sq + C1) * (sigma1_sq + sigma2_sq + C2)
    ssim_map = num / den
    return float(np.mean(ssim_map))

def main():
    if len(sys.argv) < 2:
        screenshot_path = "shot.png"
    else:
        screenshot_path = sys.argv[1]

    if not os.path.exists(screenshot_path):
        print(f"Error: Screenshot {screenshot_path} does not exist.")
        sys.exit(1)

    target_full = Image.open(TARGET_PATH)
    # Real target screen rectangle inside high-res render: x 127-643, y 127-1263 (516 x 1136 px)
    target_crop = target_full.crop((127, 127, 643, 1263))

    shot = Image.open(screenshot_path)
    sw, sh = shot.size
    print(f"Screenshot size: {sw}x{sh}")

    # Scale target crop to screenshot width
    scale = sw / target_crop.width
    th_scaled = int(round(target_crop.height * scale))
    target_scaled = target_crop.resize((sw, th_scaled), Image.Resampling.LANCZOS)

    # Device height alignment:
    # Everything above stat cards (y <= 610 dp) aligned from top.
    # Stat cards and bottom bar aligned from bottom.
    split_dp = 610.0
    split_y_target = int(round(split_dp * (target_crop.height / 907.0) * scale))

    extra_h = sh - th_scaled
    print(f"Target scaled height: {th_scaled}, Screen height: {sh}, Extra gap height: {extra_h} px")

    aligned_target = Image.new("RGB", (sw, sh), (245, 249, 252))
    # Top portion
    top_target = target_scaled.crop((0, 0, sw, split_y_target))
    aligned_target.paste(top_target, (0, 0))
    # Bottom portion shifted down by extra_h
    bottom_target = target_scaled.crop((0, split_y_target, sw, th_scaled))
    aligned_target.paste(bottom_target, (0, split_y_target + extra_h))

    # Save aligned target
    aligned_target.save("/tmp/target_aligned.png")

    # Arrays
    shot_rgb = shot.convert("RGB")
    arr_shot = np.array(shot_rgb, dtype=np.float32) / 255.0
    arr_target = np.array(aligned_target, dtype=np.float32) / 255.0

    # Side-by-side
    side_by_side = Image.new("RGB", (sw * 2, sh))
    side_by_side.paste(aligned_target, (0, 0))
    side_by_side.paste(shot_rgb, (sw, 0))
    side_by_side.save("comparison_side_by_side.png")

    # 50% Blend
    blend_arr = (arr_shot * 0.5 + arr_target * 0.5) * 255.0
    Image.fromarray(blend_arr.astype(np.uint8)).save("comparison_blend.png")

    # Difference Heatmap
    diff = np.abs(arr_shot - arr_target)
    diff_mag = np.mean(diff, axis=2)
    diff_vis = (np.clip(diff_mag * 4.0, 0.0, 1.0) * 255.0).astype(np.uint8)
    heatmap = Image.fromarray(diff_vis)
    heatmap.save("comparison_heatmap.png")

    dp_scale = sw / 412.0
    def dp_rect(x1, y1, x2, y2, from_bottom=False):
        px1 = int(round(x1 * dp_scale))
        px2 = int(round(x2 * dp_scale))
        if from_bottom:
            py1 = sh - int(round(y1 * dp_scale))
            py2 = sh - int(round(y2 * dp_scale))
            py1, py2 = min(py1, py2), max(py1, py2)
        else:
            py1 = int(round(y1 * dp_scale))
            py2 = int(round(y2 * dp_scale))
        return (px1, py1, px2, py2)

    # Grayscale
    gray_shot = np.dot(arr_shot[...,:3], [0.2989, 0.5870, 0.1140])
    gray_target = np.dot(arr_target[...,:3], [0.2989, 0.5870, 0.1140])

    regions = {
        "Kidney Artwork": dp_rect(120, 240, 290, 420),
        "Left Vial & Pipe": dp_rect(15, 300, 95, 605),
        "Right Vial & Pipe": dp_rect(320, 300, 395, 605),
        "Header & Date Pill": dp_rect(20, 40, 392, 220),
        "Stat Cards": dp_rect(20, 300, 392, 110, from_bottom=True),
        "Bottom Bar": dp_rect(20, 105, 392, 25, from_bottom=True),
    }

    print("\n--- SSIM Region Report (Standard Gaussian Window) ---")
    results = {}
    for name, (x1, y1, x2, y2) in regions.items():
        r_shot = gray_shot[y1:y2, x1:x2]
        r_target = gray_target[y1:y2, x1:x2]
        val = ssim_standard(r_shot, r_target)
        target_ssim = 0.93 if "Artwork" in name or "Vial" in name else 0.97
        passed = "PASS" if val >= target_ssim else "FAIL"
        results[name] = (val, target_ssim, passed)
        print(f"{name:20s}: SSIM = {val:.4f} (Threshold: {target_ssim:.2f}) -> {passed}")

    print("\nOutput images saved:")
    print(" - comparison_side_by_side.png")
    print(" - comparison_blend.png")
    print(" - comparison_heatmap.png")

if __name__ == "__main__":
    main()
